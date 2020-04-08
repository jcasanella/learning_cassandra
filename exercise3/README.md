# Understanding Partitions

Be sure Apache Cassandra is running before doing these exercises.

```
docker exec my-dse bin/dsetool status
```

1. View the metadata for the videos table you created earlier and try to answer the following questions.

* What is the partition key?
* How many partitions are in this table?

```
USE killrvideo;
DESC videos;

docker container exec my-dse bin/nodetool tablestats killrvideo.videos;
docker container exec my-dse bin/nodetool ring
docker container exec my-dse bin/nodetool getendpoints killrvideo videos '245e8024-14bd-11e5-9743-8238356b7e32'
```
**Note:** the last command shows where the key is stored.

2. Execute the following query to view the partitioner token value for each video id.

```
SELECT token(video_id), video_id FROM videos;
```

3. Take a look to the file **videos-by-tag.csv** and create a table that will store this data partitioned by tags. With this given data set, there should be two partitions, one for each tag. Call your table `videos_by_tag`.

```
CREATE TABLE videos_by_tag(
   tag text,
   video_id timeuuid,
   added_date timestamp,
   Title text,
   PRIMARY KEY((tag), video_id)
   );

DESC videos_by_tag;
```

4. Ingest the **videos-by-tag.csv** located in /data-csv to the videos-by-tag table, using the `COPY` command.

```
COPY killrvideo.videos_by_tag FROM '/data-csv/videos-by-tag.csv' WITH DELIMITER=',' AND HEADER=true;
SELECT * FROM videos_by_tag;
 ```

5. Write a `SELECT` statement to retrieve all rows tagged with **cassandra** and another one to find all videos tagged with **datastax**.

```
SELECT * FROM videos_by_tag WHERE tag ='cassandra';
SELECT * FROM videos_by_tag WHERE tag ='datastax';
```

6. Write a query to retrieve the video having a title of Cassandra Intro.

```
SELECT * FROM videos_by_tag WHERE Title = 'Cassandra Intro' ALLOW FILTERING;
```