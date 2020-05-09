# Creating Secondary Indexes

## Overview

### Secondary Indexes

Currently, the only way to query a column without specifying the partition key is to use secondary indexes, but they are not a substitute for the denormalization of data into new tables as they are not fit for high cardinality data.

Let’s say that we have the following users table:

```
CREATE TABLE users(
    user_id bigint,
    firstname text,
    lastname text,
    ...
    country text,
    ...
    PRIMARY KEY(user_id) 
);
```

Such table structure only allows you to lookup user by user_id only. If we create a secondary index on the column country, the index would be a hidden table with the following structure

```
CREATE TABLE country_index(
    country text,
    user_id bigint,
    PRIMARY KEY((country), user_id)
);
```

The main difference with a normal Cassandra table is that the partition of country_index would not be distributed using the cluster-wide partitioner. Secondary index in Cassandra, unlike Materialized Views, is a distributed index. This means that the index itself is co-located with the source data on the same node. By co-locating the index data on the same node as source data, a write to a table with index just costs an extra local mutation when flushing original data to SSTables.

The write path to a table having native secondary index is exactly the same as for a normal table with respect to commit log. Whenever a mutation is applied to base table in memory (memtable), it is dispatched as notification to all registered indices on this table so that each index implementation can apply the necessary processing.

The native secondary index implementation just creates an inverted index for the hidden index table. It handles 3 types of operations:

* insert of new CQL row: the index just creates a new entry (partition key + clustering columns) into the index table.
* update of CQL row: only occurs IF AND ONLY IF the new mutation is replacing a value that is still contained in the memtable. Bbecause Cassandra still has the previous value to be indexed, it will pass the previous and new value to the secondary index. The index manager will then remove the entry for the previous indexed value and add a new one for the new indexed value.
* delete of CQL row: the secondary index just writes a tombstone to the index entry
. 

Because of how it is implemented cluster-wide, all secondary index implementations work best when Cassandra can narrow down the number of nodes to query (e.g. narrow down the token ranges to query). This target can be achieved if the client query restricts the partition key:

1. with a single value (WHERE partition = xxx). This is the most efficient way to use secondary index because the coordinator only needs to query 1 node (+ replicas depending on the requested consistency level)
2. with a list of partition keys (WHERE partition IN (aaa, bbb, ccc)). This is still quite efficient because the number of nodes to be queried is bounded by the number of distinct values in the IN clause
3. with a range of token (WHERE token(partition) ≥ xxx AND token(partition) ≤ yyy). This token range restriction avoids querying all primary replicas in the cluster. Of course, the narrower the token range restriction, the better it is

There are some well known anti-patterns to avoid when using native secondary index:

1. avoid very low cardinality index e.g. index where the number of distinct values is very low. A good example is an index on the gender of an user. On each node, the whole user population will be distributed on only 2 different partitions for the index: MALE & FEMALE. If the number of users per node is very dense (e.g. millions) we’ll have very wide partitions for MALE & FEMALE index, which is bad
2. avoid very high cardinality index. For example, indexing user by their email address is a very bad idea. Generally an email address is used by at most 1 user. So there are as many distinct index values (email addresses) as there are users. When searching user by email, in the best case the coordinator will hit 1 node and find the user by chance. The worst case is when the coordinator hits all primary replicas without finding any answer
3. avoid indexing a column which is updated frequently. By design the index data are stored in a Cassandra table and Cassandra data structure is designed for immutability. Indexing frequently updated data will increase write amplification (for the base table + for the index table)

If you need to index a column whose cardinality is a 1-to-1 relationship with the base row (for example an email address for an user), you can use Materialized Views instead. 

### Materialized Views

Suppose we want to track the high scores for players of several games. We have a number of queries that we would like to be able to answer:

1. Given a game, who has the highest score, and what is it?
2. Given a game and a day, who had the highest score, and what was it?
3. Given a game and a month, who had the highest score, and what was it?

Materialized views maintain a correspondence of one CQL row each in the base and the view, so we need to ensure that each CQL row which is required for the views will be reflected in the base table's primary keys. 

1. For the first query, we will need the game, the player, and their highest score. 
2. For the second, we will need the game, the player, their high score, as well the day, the month, and the year of that high score. 
3. For the final query, we need everything from the second except the day. 
   
The second query will be the most restrictive, so it determines the primary key we will use. A user can update their high score over the course of day, so we only need to track the highest score for a particular day.


```
CREATE TABLE scores
(
  user TEXT,
  game TEXT,
  year INT,
  month INT,
  day INT,
  score INT,
  PRIMARY KEY (user, game, year, month, day)
)
```

Next, we'll create the view which presents the all time high scores. To create the materialized view, we provide a simple select statement and the primary key to use for this view. Specifying the CLUSTERING ORDER BY allows us to reverse sort the high score so we can get the highest score by simply selecting the first item in the partition.

```
CREATE MATERIALIZED VIEW alltimehigh AS
       SELECT user FROM scores
       WHERE game IS NOT NULL AND score IS NOT NULL AND user IS NOT NULL AND year IS NOT NULL AND month IS NOT NULL AND day IS NOT NULL
       PRIMARY KEY (game, score, user, year, month, day)
       WITH CLUSTERING ORDER BY (score desc);
```

To query the daily high scores, we create a materialized view that groups the game title and date together so a single partition contains the values for that date. We do the same for the monthly high scores.

```
CREATE MATERIALIZED VIEW dailyhigh AS
       SELECT user FROM scores
       WHERE game IS NOT NULL AND year IS NOT NULL AND month IS NOT NULL AND day IS NOT NULL AND score IS NOT NULL AND user IS NOT NULL
       PRIMARY KEY ((game, year, month, day), score, user)
       WITH CLUSTERING ORDER BY (score DESC)

CREATE MATERIALIZED VIEW monthlyhigh AS
       SELECT user FROM scores
       WHERE game IS NOT NULL AND year IS NOT NULL AND month IS NOT NULL AND score IS NOT NULL AND user IS NOT NULL AND day IS NOT NULL
       PRIMARY KEY ((game, year, month), score, user, day)
       WITH CLUSTERING ORDER BY (score DESC)
```

We just insert the data into the scores table, and Cassandra will populate the materialized views accordingly.

```
INSERT INTO scores (user, game, year, month, day, score) VALUES ('pcmanus', 'Coup', 2015, 05, 01, 4000)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('jbellis', 'Coup', 2015, 05, 03, 1750)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('yukim', 'Coup', 2015, 05, 03, 2250)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('tjake', 'Coup', 2015, 05, 03, 500)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('jmckenzie', 'Coup', 2015, 06, 01, 2000)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('iamaleksey', 'Coup', 2015, 06, 01, 2500)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('tjake', 'Coup', 2015, 06, 02, 1000)
INSERT INTO scores (user, game, year, month, day, score) VALUES ('pcmanus', 'Coup', 2015, 06, 02, 2000)
```

We can now search for users who have scored the highest ever on our games: `SELECT user, score FROM alltimehigh WHERE game = 'Coup' LIMIT 1`
And the daily high score `SELECT user, score FROM dailyhigh WHERE game = 'Coup' AND year = 2015 AND month = 06 AND day = 01 LIMIT 1`

We can also delete rows from the base table and the materialized view's records will be deleted.

When not to use Materialized Views:

* Materialized views do not have the same write performance characteristics that normal table writes have. The materialized view requires an additional read-before-write, as well as data consistency checks on each replica before creating the view updates. These additions overhead, and may change the latency of writes.
* If the rows are to be combined before placed in the view, materialized views will not work. 
* Low cardinality data will create hotspots around the ring
* If the partition key of all of the data is the same, those nodes would become overloaded. 
* WHERE clauses, ORDER BY, and functions aren't available with materialized views
* If there will be a large number of partition tombstones, the performance may suffer; the materialized view must query for all of the current values and generate a tombstone for each of them. The materialized view will have one tombstone per CQL row deleted in the base table

https://www.datastax.com/blog/2015/06/new-cassandra-30-materialized-views

## Exercises

Killrvideo has learned that users want to be able to determine an actor's name based on the video the actor appeared in and the name of the character that actor played. Also, sometimes they want to determine the actor's name based on the character's name alone because they don't recall the name of the movie. 

1. Launch cqlsh and switch to killr_video keyspace. Create a secondary index on the actors_by_video.

```
USE killr_video;

CREATE INDEX ractor_name ON actors_by_video (actor_name);
CREATE INDEX rcharacter_name ON actors_by_video (character_name);
```

2. Query the table as follows to validate your work:
   
* Search for all rows where the video_id = 87c645e8-0ef2-11e5-98f3-8438355b7e3a AND the character is named Kelly La Fonda.
* Search for all rows where the character name is George McFly.

```
SELECT * FROM actors_by_video WHERE video_id = 87c645e8-0ef2-11e5-98f3-8438355b7e3a AND character_name = 'Kelly La Fonda';
SELECT * FROM actors_by_video WHERE character_name = 'George McFly';

SELECT * FROM actors_by_video WHERE actor_name = 'Crispin Glover';
```

