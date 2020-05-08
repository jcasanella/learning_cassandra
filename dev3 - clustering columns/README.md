# Clustering Columns

In this exercise you will create a videos_by_tag_year table that allows range scans and ordering by year.

There have been some interesting wrinkles in your quest to understand how Cassandra and CQL work. Although you have been able to complete your tasks to the letter, your team cannot query based on tag and year. Fortunately, your new understanding of clustering columns will help to improve your design. You decide to build a table that allows querying by tag and added_year. The columns are as follows:

| Column Name | Data Type |
| ----------- | --------- |
| tag | text |
| added_year | int |
| video_id | uuid |
| added_date | timestamp |
| title | text |
| user_id |	uuid |

1. In order to demonstrate a little bit more about clustering columns, we are first going to show you upserts.  To become an upsert ninja, create the following (bad) table with the (crummy) primary key. As an aside, use DESCRIBE TABLE to view the structure of your bad_videos_by_tag_year table.

```
USE killrvideo;

CREATE TABLE bad_videos_by_tag_year (
     tag text,
     added_year int,
     added_date timestamp,
     title text,description text,
     user_id uuid,
     video_id timeuuid,
     PRIMARY KEY ((video_id))
);

DESC bad_videos_by_tag_year;
```

Output desc:

```
CREATE TABLE killrvideo.bad_videos_by_tag_year (
    video_id timeuuid PRIMARY KEY,
    added_date timestamp,
    added_year int,
    description text,
    tag text,
    title text,
    user_id uuid
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99PERCENTILE';
```

**NOTE**: Notice the column order differs from the CREATE TABLE statement. Cassandra orders columns by partition key, clustering columns (shown later), and then alphabetical order of the remaining columns.

2. Execute the following COPY command to import `videos_by_tag_year.csv` file and count the number of rows inserted.  **NOte**: We must specify the header in the copy command because does not match the number of columns with the number of fields of this file.

```
docker cp assets220/ds220-6.0-labwork/labwork/clustering/videos_by_tag_year.csv dse6_node1:/.

USE killrvideo;

COPY bad_videos_by_tag_year (tag, added_year, video_id, added_date, description, title, user_id) FROM '/videos_by_tag_year.csv' WITH HEADER=true;

SELECT count(*) FROM bad_videos_by_tag_year;
```

**Notice** the number of rows in the `bad_videos_by_tag_year` does not match the number of rows imported from `videos_by_tag_year.csv`. Since `videos_by_tag_year.csv` duplicates video_id for each unique 'tag' and 'year' per video, Cassandra upserted several records during the COPY. video_id is not a proper partition key for this scenario.

3. Drop the table

```
DROP TABLE bad_videos_by_tag_year;
```

4. Create a table with the columns above to facilitate querying for videos by tag within a given year range returning the results in descending order by year. And load the data again. Check the number of rows inserted.

```
CREATE TABLE videos_by_tag_year (
     tag text,
     added_year int,
     video_id timeuuid,
     added_date timestamp,
     description text,
     title text,
     user_id uuid,
     PRIMARY KEY ( (tag), added_year, video_id)
) WITH CLUSTERING ORDER BY (added_year DESC, video_id ASC );

COPY videos_by_tag_year (tag, added_year, video_id, added_date, description, title, user_id) FROM '/videos_by_tag_year.csv' WITH HEADER=true;

SELECT count(*) FROM videos_by_tag_year;
```

**Note**: It matches the number of rows inserted in the table with the number of rows that contains the table.

5. Try running queries on the videos_by_tag_year table to query on a specific tag and added year.

Example queries:

| tag |	added_year |
| --- | ---------- |
| trailer | 2015 |
| cql |	2014 |
| spark | 2014 |

Try querying for all videos with tag "cql" added before the year 2015. Notice you can do range queries on clustering columns.
Try querying for all videos added before 2015. The query will fail. What error message does cqlsh report? Why did the query fail whereas the previous query worked?

```
SELECT * FROM videos_by_tag_year WHERE tag = 'trailer' AND added_year=2015;
SELECT * FROM videos_by_tag_year WHERE tag = 'cql' AND added_year=2014;
SELECT * FROM videos_by_tag_year WHERE tag = 'spark' AND added_year=2014;

SELECT * FROM videos_by_tag_year WHERE tag = 'trailer' AND added_year<2015;
SELECT * FROM videos_by_tag_year WHERE added_year<2015;
```

This fails because the partition column tag was not queried, this means C* need to process all of the partitions in the table.