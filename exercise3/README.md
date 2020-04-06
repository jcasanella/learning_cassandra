Be sure Apache Cassandra is running before doing these exercises.

```
docker exec my-dse bin/dsetool status
```

view the metadata for the videos table you created earlier.

```
use killrvideo;
desc videos;
```

What is the partition key?
How many partitions are in this table?

```
docker container exec my-dse bin/nodetool tablestats killrvideo.videos;
docker container exec my-dse bin/nodetool ring
docker container exec my-dse bin/nodetool getendpoints killrvideo videos '245e8024-14bd-11e5-9743-8238356b7e32'
```
the last command shows where is stored the key.


Execute the following query to view the partitioner token value for each video id.

```
SELECT token(video_id), video_idFROM videos;
```

Take a look to the file videos-by-tag.csv and CREATE a TABLE that will store this data partitioned by tags. With this given data set, there should be two partitions, one for each tag. Call your table videos_by_tag.

```
CREATE TABLE videos_by_tag(
   tag text,
   video_id timeuuid,
   added_date timestamp,
   Title text,
   PRIMARY KEY((tag), video_id)
   );

desc videos_by_tag;
```

Ingest the videos-by-tag.csv to the videos-by-tag table, using the  copy command.

```
docker cp .\assets201\videos-by-tag.csv my-dse:/opt/dse/data
copy killrvideo.videos_by_tag from '/opt/dse/data/videos-by-tag.csv' with delimiter=',' and header=true ;
select * from videos_by_tag;
 ```

Write a SELECT statement to retrieve all rows tagged with cassandra.

```
SELECT * FROM videos_by_tag WHERE tag ='cassandra';
```

find all videos tagged with datastax

```
SELECT * FROM videos_by_tag WHERE tag ='datastax';
```

write a query to retrieve the video having a title of Cassandra Intro.

```
SELECT * FROM videos_by_tag WHERE Title = 'Cassandra Intro' ALLOW FILTERING;
```
