# Denormalizing

In this exercise you will learn to create tables to support querying for videos by actor or genre.

Joins are not optimal and not supported in Cassandra. With the next schemas we need to create the correct tables schemas for the queries to be run.

The video metadata is similar to what was in the video sharing domain:

| Column Name | Data Type |
| ----------- | --------- |
| video_id | timeuuid |
| added_date | timestamp |
| description | text |
| encoding | video_encoding |
| tags | set |
| title | text |
| user_id |	uuid |

There is also the additional following metadata:

| Column Name |	Data Type |
| actor	| text |
| character | text |
| genre | text |

With this metadata, the data model must support the following queries:

* Q1: Retrieve videos in which an actor has appeared (newest first).
* Q2: Retrieve videos within a particular genre (newest first).

1. Create a new table called videos_by_actor which will support query Q1. Look a little more closely at the table above. Our encoding column is a User Defined Type (or UDT for short). Copy and paste this code to create the UDT so that our create table works correctly.

```
USE killrvideo;

CREATE TYPE IF NOT EXISTS video_encoding (
     encoding TEXT,
     height INT,
     width INT,
     bit_rates SET<TEXT>
);

CREATE TABLE videos_by_actor (
     actor text,
     added_date timestamp,
     video_id timeuuid,
     character_name text,
     description text,
     encoding frozen<video_encoding>, 
     tags set<text>,
     title text,
     user_id uuid,
     PRIMARY KEY ( (actor), added_date, video_id, character_name )
) WITH CLUSTERING ORDER BY (added_date DESC, video_id ASC, character_name ASC );
```

2. Load `videos_by_actor.csv` into the videos_by_actor table using the COPY command.

```
docker cp assets220/ds220-6.0-labwork/labwork/denormalization/videos_by_actor.csv dse6_node1:/.

USE killrvideo;

COPY videos_by_actor (actor, added_date, video_id, character_name, description, encoding, tags, title, user_id) FROM '/videos_by_actor.csv' WITH HEADER = true;

SELECT count(*) FROM videos_by_actor; 
```

*Note*: number of rows matches with the number of rows inserted/

3. Run a query to retrieve the video information for a particular actor (Tom Hanks, Denzel Washington, or see if your favorite actor is in there). Try SELECTing just the actor and the added_date columns. Notice the order of added dates.

```
SELECT * FROM videos_by_actor WHERE actor = 'Tom Hanks';
SELECT * FROM videos_by_actor WHERE actor = 'Tom Hanks' AND added_date < '1998-07-01';
```

4. Create a new table called videos_by_genre which will support query Q2.

```
CREATE TABLE videos_by_genre (
     genre text,
     added_date timestamp,
     video_id timeuuid,
     description text,
     encoding frozen<video_encoding>, 
     tags set<text>,
     title text,
     user_id uuid,
     PRIMARY KEY ( (genre), added_date, video_id)
) WITH CLUSTERING ORDER BY ( added_date DESC, video_id ASC );
```

5. Load `videos_by_genre.csv` into the videos_by_genre table using the COPY command. Run a query to retrieve the video information for a particular genre (Future noir, Time travel).

```
docker cp assets220/ds220-6.0-labwork/labwork/denormalization/videos_by_genre.csv dse6_node1:/.

USE killrvideo;

COPY videos_by_genre(genre, added_date, video_id, description, encoding, tags, title, user_id) FROM '/videos_by_genre.csv' WITH HEADER = true;

SELECT count(*) FROM videos_by_genre;

SELECT * FROM videos_by_genre WHERE genre = 'Future noir';
SELECT * FROM videos_by_genre WHERE genre = 'Future noir' AND added_date < '2006-01-01';
```