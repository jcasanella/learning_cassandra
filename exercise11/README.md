# Hinted Handoff

## Overview

Availability can affect data consistency. Hinted-handoff helps Apache Cassandra(TM) maintain consistency even when a node is down

## Exercises

1. Determine which nodes are responsible for the cassandra partition in videos_by_tag by executing the following command in the terminal and stop 2 of them.

```
nodetool getendpoints killrvideo videos_by_tag 'cassandra'
nodetool stopdaemon
```

2. Change consistency level to ANY and insert a row

```
USE killrvideo;
CONSISTENCY ANY;
INSERT INTO videos_by_tag(tag, added_date, video_id, title) VALUES ('cassandra', '2016-2-11', uuid(), 'Cassandra, Take Me Home');
```

The write will succeed even though both replica nodes are down. The one and only node left in the cluster stores the writes as hints for the two replica nodes until they come back online.

If we check the hints folder, we can see how Cassandra stores the hints:

```
ls -la /var/lib/cassandra/hints
total 24
drwxr-xr-x 2 dse dse 4096 May  2 11:11 .
drwxrwxrwx 7 dse dse 4096 Apr 12 15:43 ..
-rw-r--r-- 1 dse dse 7004 May  2 11:15 93e2f0eb-e71b-4d0d-940f-bd6dd9453e30-1588417863698-1.hints
-rw-r--r-- 1 dse dse 7004 May  2 11:15 f4b2302a-861d-4eb5-aa0d-aa7cc330a2e0-1588417863698-1.hints
```

3. Start up one of the nodes, wait for some time to be sure the node is added to the ring, and check again the hints.

```
ls -la /var/lib/cassandra/hints
total 32
drwxr-xr-x 2 dse dse  4096 May  2 11:20 .
drwxrwxrwx 7 dse dse  4096 Apr 12 15:43 ..
-rw-r--r-- 1 dse dse 20684 May  2 11:21 93e2f0eb-e71b-4d0d-940f-bd6dd9453e30-1588417863698-1.hints
```

4. Now bring up the remaining offline node. Wait for it to start up before proceeding. Again,you will notice some file activity in the hints/ folder, and eventually the folder will clear.

```
ls -la /var/lib/cassandra/hints
total 8
drwxr-xr-x 2 dse dse 4096 May  2 11:26 .
drwxrwxrwx 7 dse dse 4096 Apr 12 15:43 ..
```