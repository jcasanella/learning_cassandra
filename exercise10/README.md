# Consistency Levels

## Overview

Lower consistencies levels like one improve throughput and availability at expenses of data correctness by not involving other replicas for the operation. And that's the primary design of Cassandra. (stora large volumes of data at low consistency and high availability). However, when correctness of data starts becoming important we must increase reads and writes levels of consistency.

Strong consistency typically is expressed as `W + R > RF`, where W is the write consistency, R is the read consistency and RF the Replication Factor. For example if RF is 3, a QUORUM request will require responses from at least 2 of 3 replicas. If QUORUM is used for both reads and writes (which means R=2 W=2) at least one of the replicas is guaranteed to participate in both the write and the read request.

In a multiple DataCenter, LOCAL_QUORUM should be used to ensure that reads can see the latest write from within the same data center.

**Quorum Writes & Reads != Strong consistency.** Will serve dirty data in presence of failed writes. A quorum read can serve correct data when the quorum write preceding it succeeds completely. However, writes that fail because only a partial set of replicas are updated could lead to two different readers seeing two different values of data.

## Exercises

1. Check consistency level of the keyspace `killrvideo` and change the consistency level to 2.

```
USE killrvideo;
CONSISTENCY;

Current consistency level is ONE.

CONSISTENCY TWO;
CONSISTENCY;

Current consistency level is TWO.
```

2. etrieve the cassandra partition from the videos_by_tag

```
SELECT * FROM videos_by_tag WHERE tag = 'cassandra';
```

3. Determine which nodes hold the replicas for the cassandra partition tag value in the videos_by_tag table

```
nodetool getendpoints killrvideo videos_by_tag 'cassandra'
```

4. Drop one node and run the query from point 2. Because consistency level is 2, it works. Stop another instance and run again the same query, should fail beause not possible to match the consistency level.

```
nodetool stopdaemon

USE killrvideo;
CONSISTENCY TWO;

SELECT * FROM killrvideo.videos_by_tag WHERE tag = 'cassandra';

USE killrvideo;
CONSISTENCY TWO;

SELECT * FROM killrvideo.videos_by_tag WHERE tag = 'cassandra';
NoHostAvailable: 

CONSISTENCY ONE;
SELECT * FROM killrvideo.videos_by_tag WHERE tag = 'cassandra';
```

After change the consistency level to one. The query was able to run due the number of replicas.

5. Lets do a similar exercise but with writings. Change the consistency level to 2 and try to insert a row.


```
CONSISTENCY TWO;
INSERT INTO killrvideo.videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', '2016-2-8', uuid(), 'Me Lava Cassandra');
NoHostAvailable: 

CONSISTENCY ONE;
INSERT INTO killrvideo.videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', '2016-2-8', uuid(), 'Me Lava Cassandra');
```

Again after change the consistency level to one, the insert query worked fine. The reason is because we ask to get an ack from one peer.

6 .Restart one of the nodes, and change consistency level to 2, and insert another row and select the rows by key.

```
CONSISTENCY TWO;
INSERT INTO killrvideo.videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', '2016-2-12', uuid(), 'Me love Cassandra');
SELECT * FROM killrvideo.videos_by_tag WHERE tag = 'cassandra';
```

7. Change consistency level to  one and put it down one of the peers.

```
nodetool stopdaemon

CONSISTENCY ONE;
SELECT * FROM killrvideo.videos_by_tag WHERE tag = 'cassandra';
```

Notice the query succeeds and the new record is still present. This is because of read repair

8. Update the record to a new title by executing the CQL command that follows. You will have to copy and paste the video_id from the result set above into the UPDATE's WHERE clause. And verify the update has been done.

Note: replace the UUID for the correct one

```
UPDATE killrvideo.videos_by_tag SET title = 'Me LovEEEEEEEE Cassandra' WHERE tag = 'cassandra' AND added_date = '2016-02-08' AND video_id = 2083eaba-da75-4ce5-9142-b31f28c88364;
SELECT * FROM killrvideo.videos_by_tag WHERE tag = 'cassandra';
```