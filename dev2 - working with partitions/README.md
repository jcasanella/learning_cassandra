# Working with Partitions

In this exercise, you will create a new table that allows querying videos by title and year using a composite partition key.

Your peers need to query videos based on `title` and `added_year`. The new columns for this table are:

| Column Name |	Data Type |
| ----------- | --------- |
| title | text |
| added_year | int |
| added_date | timestamp |
| description |	text |
| user_id |	uuid |
| video_id | uuid |

1. Create a second table in the killrvideo keyspace called `videos_by_title_year` with the structure shown in above table. Be sure users can query this table on both `title` and `added_year` by combining them into the partition key.

```
CREATE TABLE videos_by_title_year (
    title text,
    added_year int,
    added_date timestamp,
    description text,
    user_id uuid,
    video_id uuid,
    PRIMARY KEY((title, added_year))
);

DESC tables;
```

3. Load the data from the videos_by_title_year.csv file using the COPY command

```
docker cp assets220/ds220-6.0-labwork/labwork/partitions/videos_by_title_year.csv dse6_node1:/.

cqlsh

USE killrvideo;

COPY videos_by_title_year (title, added_year, added_date, description, user_id, video_id) FROM '/videos_by_title_year.csv' WITH HEADER=true;
```

4. Try running queries on the videos_by_title_year table to query on a specific `title` and `added_year`.

Example queries:

| title | added_year |
| ----- | ---------- |
| AzureDev | 2015 |

```
SELECT * FROM videos_by_title_year WHERE title = 'AzureDev' AND
added_year = 2015;
```

5. What error does Cassandra return when you try to query on just title or just year? Why?

```
SELECT * FROM videos_by_title_year WHERE title = 'AzureDev';

InvalidRequest: Error from server: code=2200 [Invalid query] message="Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. If you want to execute this query despite the performance unpredictability, use ALLOW FILTER

SELECT * FROM videos_by_title_year WHERE added_year = 2015;

InvalidRequest: Error from server: code=2200 [Invalid query] message="Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. If you want to execute this query despite the performance unpredictability, use ALLOW FILTERING"
```