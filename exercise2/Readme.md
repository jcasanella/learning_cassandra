# Firsts steps with Cassandra

* Check if DataStax Enterprise has started

```
docker exec -it my-dse  bin/dsetool status
```

You will see something similar to this screen

```
DC: dc1             Workload: Cassandra       Graph: no
======================================================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--   Address          Load             Owns                 Token                                        Rack         Health [0,1]
UN   172.17.0.2       113.72 KiB       ?                    6377410009375034256                          rack1        0.70
```

## First steps with CQL

The purpose of this exercise is:

* Create a keyspace for KillrVideo
* Create a table to store video metadata
* Load the data for the video table from a CSV file

###Introduction 
Welcome to the **KillrVideo company!** KillrVideo hired you to build the latest and greatest video sharing application on the Internet. Your task is to ramp up on the domain and become acquainted with Apache Cassandra™. To start, you decided to look into creating a table schema and to load some video data.

The video metadata is made up of:
| Column Name | Data Type |
------------- | --------- |
| video_id | timeuuid |
| added_date |	timestamp |
| Title | Text |

1. Create a keyspace called `killrvideo`. Use `SimpleStrategy` for the replication class with a replication factor of `one`. 

```
docker exec -it my-dse bin/cqlsh

CREATE KEYSPACE killrvideo
WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

DESCRIBE killrvideo;
```

`Note:` if you run in a Datastax Cluster, change the replication factor. In this repository there's a docker-compose that builds a Datastax cluster.

```
CREATE KEYSPACE killrvideo
WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};
```

Switch to the newly created keyspace

```
USE killrvideo;
```

2. Create a single table called videos with the same structure as shown above. `video_id` is the primary key

```
CREATE TABLE videos(
   video_id timeuuid PRIMARY KEY,
   added_date timestamp,
   Title text
   );

DESCRIBE tables;
```

Other option to see the tables is:

```
USE system_schema;

SELECT keyspace_name,table_name FROM tables WHERE keyspace_name = 'killrvideo';
```

3. Manually insert the following rows, using `INSERT` command. 
   
| video_id | added_date | title |
| -------- | ---------- | ----- |
| 1645ea59-14bd-11e5-a993-8138354b7e31 | 2014-01-29 | Cassandra History |
| 245e8024-14bd-11e5-9743-8238356b7e32 | 2012-04-03 | Cassandra & SSDs |
| 3452f7de-14bd-11e5-855e-8738355b7e3a | 2013-03-17 |	Cassandra Intro |
| 4845ed97-14bd-11e5-8a40-8338255b7e33	| 2013-10-16 | DataStax DevCenter |
| 5645f8bd-14bd-11e5-af1a-8638355b8e3a | 2013-04-16 | What is DataStax Enterprise? |

```
insert into videos (video_id, added_date, title) values (1645ea59-14bd-11e5-a993-8138354b7e31, '2014-01-29', 'Cassandra History');
insert into videos (video_id, added_date, title) values (245e8024-14bd-11e5-9743-8238356b7e32, '2012-04-03', 'Cassandra & SSDs');
insert into videos (video_id, added_date, title) values (3452f7de-14bd-11e5-855e-8738355b7e3a, '2013-03-17', 'Cassandra Intro');
insert into videos (video_id, added_date, title) values (4845ed97-14bd-11e5-8a40-8338255b7e33, '2013-10-16', 'DataStax DevCenter');
insert into videos (video_id, added_date, title) values (5645f8bd-14bd-11e5-af1a-8638355b8e3a, '2013-04-16', 'What is DataStax Enterprise?');
```

4. Write a `SELECT` statement to verify the table is not empty.

```
SELECT * FROM videos;
SELECT * FROM videos WHERE added_date > '2013-04-16' ALLOW FILTERING;
```

5. Let's remove the data you inserted using the `TRUNCATE` command.

```
TRUNCATE videos;
SELECT * FROM videos;
```

6. Import the file videos.csv located in `/data-csv` to your table, using the command `COPY`

```
COPY killrvideo.videos FROM '/data-csv/videos.csv' WITH DELIMITER=',' AND HEADER=true;
SELECT * FROM videos;
 ```