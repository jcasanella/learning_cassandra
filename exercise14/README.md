# Compaction

## Exercises

1. Let's recreate the **killrvideo** keyspace and also create the **videos_by_tag** database. Insert a row and flush the Memtableto an SStable.

```
DROP KEYSPACE killrvideo;

CREATE KEYSPACE killrvideo WITH replication = {'class':'SimpleStrategy', 'replication_factor': 3};

USE killrvideo;
CREATE TABLE videos_by_tag (tag TEXT, video_id UUID, added_date TIMESTAMP, title TEXT, PRIMARY KEY ((tag), video_id));

INSERT INTO videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Master');

nodetool flush
```

Let's investigate the SSTable in the node's data directory. 

```
ls /var/lib/cassandra/data/killrvideo/videos_by_tag*

/var/lib/cassandra/data/killrvideo/videos_by_tag-b467fe017cf411ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-b978c7717cfa11ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-c1e0a0b18c8e11eab4a94be3d7b5b7a4:
aa-1-bti-CompressionInfo.db  aa-1-bti-Digest.crc32  aa-1-bti-Partitions.db  aa-1-bti-Statistics.db  backups
aa-1-bti-Data.db             aa-1-bti-Filter.db     aa-1-bti-Rows.db        aa-1-bti-TOC.txt
```

2. Repeat the process. Insert a row and flush into the SSTable and check the SSTables.

```
USE killrvideo;
INSERT INTO killrvideo.videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Genius');

nodetool flush

ls /var/lib/cassandra/data/killrvideo/videos_by_tag*

/var/lib/cassandra/data/killrvideo/videos_by_tag-b467fe017cf411ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-b978c7717cfa11ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-c1e0a0b18c8e11eab4a94be3d7b5b7a4:
aa-1-bti-CompressionInfo.db  aa-1-bti-Filter.db      aa-1-bti-Statistics.db       aa-2-bti-Data.db       aa-2-bti-Partitions.db  aa-2-bti-TOC.txt
aa-1-bti-Data.db             aa-1-bti-Partitions.db  aa-1-bti-TOC.txt             aa-2-bti-Digest.crc32  aa-2-bti-Rows.db        backups
aa-1-bti-Digest.crc32        aa-1-bti-Rows.db        aa-2-bti-CompressionInfo.db  aa-2-bti-Filter.db     aa-2-bti-Statistics.db
```

3. Do again the same process.

```
USE killrvideo;
INSERT INTO killrvideo.videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Wizard');

nodetool flush

ls /var/lib/cassandra/data/killrvideo/videos_by_tag*

/var/lib/cassandra/data/killrvideo/videos_by_tag-b467fe017cf411ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-b978c7717cfa11ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-c1e0a0b18c8e11eab4a94be3d7b5b7a4:
aa-1-bti-CompressionInfo.db  aa-1-bti-Partitions.db  aa-2-bti-CompressionInfo.db  aa-2-bti-Partitions.db  aa-3-bti-CompressionInfo.db  aa-3-bti-Partitions.db  backups
aa-1-bti-Data.db             aa-1-bti-Rows.db        aa-2-bti-Data.db             aa-2-bti-Rows.db        aa-3-bti-Data.db             aa-3-bti-Rows.db
aa-1-bti-Digest.crc32        aa-1-bti-Statistics.db  aa-2-bti-Digest.crc32        aa-2-bti-Statistics.db  aa-3-bti-Digest.crc32        aa-3-bti-Statistics.db
aa-1-bti-Filter.db           aa-1-bti-TOC.txt        aa-2-bti-Filter.db           aa-2-bti-TOC.txt        aa-3-bti-Filter.db           aa-3-bti-TOC.txt
```

NOTE: When Apache Cassandra™ goes to create a fourth SSTable, Apache Cassandra™ will perform compaction.

4. Insert another row and check the SSTables.

```
USE killrvideo;
INSERT INTO killrvideo.videos_by_tag (tag, added_date, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Ninja');

nodetool flush

ls /var/lib/cassandra/data/killrvideo/videos_by_tag*

/var/lib/cassandra/data/killrvideo/videos_by_tag-b467fe017cf411ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-b978c7717cfa11ea93b54be3d7b5b7a4:
backups  snapshots

/var/lib/cassandra/data/killrvideo/videos_by_tag-c1e0a0b18c8e11eab4a94be3d7b5b7a4:
aa-5-bti-CompressionInfo.db  aa-5-bti-Digest.crc32  aa-5-bti-Partitions.db  aa-5-bti-Statistics.db  backups
aa-5-bti-Data.db             aa-5-bti-Filter.db     aa-5-bti-Rows.db        aa-5-bti-TOC.txt
```