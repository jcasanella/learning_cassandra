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
