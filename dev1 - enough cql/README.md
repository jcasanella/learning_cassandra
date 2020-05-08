# Basic CQL Fundamentals

In this exercise you will:

* Create a Keyspace for KillrVideo
* Create a table to store video metadata
* Load the data for the video table from a CSV file

The video metadata is made up of:

| Column Name |	Data Type |
| ----------- | --------- |
| video_id | timeuuid |
| added_date | timestamp |
| description |	text |
| title | text |
| user_id |	uuid |

1. In cqlsh, create a keyspace called `killrvideo` and switch to that keyspace. Use SimpleStrategy for the replication class with a replication factor of one. Remember the `USE` command switches keyspaces.

```
CREATE  KEYSPACE IF NOT EXISTS killrvideo 
WITH REPLICATION = { 
    'class' : 'SimpleStrategy', 'replication_factor' : 1
}; 

USE killrvideo;
```

2. Create a single table called videos with the same structure as shown in table above. video_id is the primary key.

```
CREATE TABLE videos (
    video_id timeuuid PRIMARY KEY,
    added_date timestamp,
    description text,
    title text,
    user_id uuid
);

DESC tables;
```

3. Load the newly created table with the videos.csv file using the COPY command.

```
docker cp assets220/ds220-6.0-labwork/labwork/cql/videos.csv dse6_node1:/.

cqlsh

USE killrvideo;

COPY videos (video_id,added_date,description,title,user_id) FROM '/videos.csv' WITH HEADER=true;

SELECT * FROM videos;
```

4. Use SELECT to COUNT(*) the number of imported rows. It should match the number of rows COPY reported as imported.

```
SELECT count(1)
FROM videos;
```

5.  Use SELECT to find a row where the `video_id = 6c4cffb9-0dc4-1d59-af24-c960b5fc3652`.

```
SELECT *
FROM videos
WHERE video_id=6c4cffb9-0dc4-1d59-af24-c960b5fc3652;
```

6.  Let's remove the data from our table using TRUNCATE.

```
TRUNCATE videos;

SELECT * FROM videos;
```