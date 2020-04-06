Drop your videos_by_tag table that you created in the previous exercise

```
use killrvideo;
drop table videos_by_tag;
desc tables;
```

create a new videos_by_tag table partitioned based on the tag. The table should also store the rows of each partition so that the newest videos are listed first within the partition.

```
CREATE TABLE videos_by_tag (
     tag text,
     video_id uuid,
     added_date timestamp,
     title text,
     PRIMARY KEY ((tag), added_date, video_id))
WITH CLUSTERING ORDER BY (added_date DESC);
```

Import the videos_by_tag.csv again via the COPY

```
docker cp .\assets201\videos-by-tag.csv my-dse:/opt/dse/data
COPY videos_by_tag(tag, video_id, added_date, title) FROM '/opt/dse/data/videos-by-tag.csv' WITH HEADER = TRUE;
select * from videos_by_tag;
 ```

 NOTE: Notice the rows are still grouped by their partition key value but ordered in descending order of added date.

 Execute your query again, but list the oldest videos first.

 ```
 SELECT * FROM videos_by_tag ORDER BY added_date ASC;
 ```

 query fails, because we must specify the partition

 ```
SELECT * FROM videos_by_tag WHERE tag='cassandra' ORDER BY added_date ASC;
 ```

 Change your query to retrieve videos made in 2013 or later.

```
SELECT * FROM videos_by_tag WHERE tag='cassandra' AND added_date > '2012-12-31' ORDER BY added_date ASC;
 ```

