# Read Path

## Exercises

1. Populate our cluster with a good chunk of data. We will use cassandra-stress to do so. Execute the following command in a terminal window. Wait for cassandra-stress to complete before continuing. Once if finishes, flush the memtables.

```
/opt/dse/resources/cassandra/tools/bin/cassandra-stress write no-warmup n=250000 -port native=9042 -rate threads=1

nodetool flush
```

2. In your terminal, navigate to the data directory for the large table that cassandra-stress wrote. Remember that cassandra-stress creates a keyspace called keyspace1 and a table called standard1. 

```
cd /var/lib/cassandra/data/keyspace1/standard1-def671218c7011eaa765d3e8a08ae426
```

And list the bloom filter files for your SSTables. 

```
ls -lh *Filter.db

-rw-r--r-- 1 dse dse 87K May  2 12:46 aa-1-bti-Filter.db
-rw-r--r-- 1 dse dse 87K May  2 12:57 aa-2-bti-Filter.db
```

3. Describe the keyspace created and look for the bloom filter section.

```
DESCRIBE keyspace keyspace1;

CREATE KEYSPACE keyspace1 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE keyspace1.counter1 (
    key blob PRIMARY KEY,
    "C0" counter,
    "C1" counter,
    "C2" counter,
    "C3" counter,
    "C4" counter
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'enabled': 'false'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99PERCENTILE';

CREATE TABLE keyspace1.standard1 (
    key blob PRIMARY KEY,
    "C0" blob,
    "C1" blob,
    "C2" blob,
    "C3" blob,
    "C4" blob
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'enabled': 'false'}
    AND crc_check_chance = 1.0
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND speculative_retry = '99PERCENTILE';
```

Note the bloom_filter_fp_chance is 0.01, meaning a 1% chance of a false positive. That's pretty good, but if we want an even smaller chance, we can trade space for it.

4. Change the percentage of bloom filter to 0.1%: `ALTER TABLE keyspace1.standard1 WITH bloom_filter_fp_chance = 0.0001;`

Now that we have changed the bloom_filter_fp_chance, we must update our SSTables and associated bloom filter files. Run the command in all the nodes:

```
nodetool upgradesstables --include-all-sstables
```

Check the size of the bloom filters. 

```
cd /var/lib/cassandra/data/keyspace1/standard1-def671218c7011eaa765d3e8a08ae426
ls -lh *Filter.db

-rw-r--r-- 1 dse dse 173K May  2 13:06 aa-3-bti-Filter.db
-rw-r--r-- 1 dse dse 173K May  2 13:06 aa-4-bti-Filter.db
```

The size of these files is larger. We traded space for a smaller chance of a false positive.

5. Lets do the same process but changing the bloom filter to 100%. 

```
ALTER TABLE keyspace1.standard1 WITH bloom_filter_fp_chance = 1.0;
nodetool upgradesstables --include-all-sstables  (run in all the nodes)
cd /var/lib/cassandra/data/keyspace1/standard1-def671218c7011eaa765d3e8a08ae426
ls -lh *Filter.db
ls: cannot access '*Filter.db': No such file or directory
```

Reason with percentage 100% we have disabled the bloom filters optimization. Check with the statistics:

```
nodetool cfstats keyspace1.standard1

Total number of tables: 53
----------------
Keyspace : keyspace1
        Read Count: 0
        Read Latency: NaN ms
        Write Count: 142210
        Write Latency: 0.020494803459672317 ms
        Pending Flushes: 0
                Table: standard1
                SSTable count: 2
                Space used (live): 33829767
                Space used (total): 33829767
                Space used by snapshots (total): 0
                Off heap memory used (total): 0
                SSTable Compression Ratio: -1.0
                Number of partitions (estimate): 70730
                Memtable cell count: 0
                Memtable data size: 0
                Memtable off heap memory used: 0
                Memtable switch count: 4
                Local read count: 0
                Local read latency: NaN ms
                Local write count: 142210
                Local write latency: 0.020 ms
                Pending flushes: 0
                Percent repaired: 0.0
                Bytes repaired: 0.000KiB
                Bytes unrepaired: 31.054MiB
                Bytes pending repair: 0.000KiB
                Bloom filter false positives: 0
                Bloom filter false ratio: 0.00000
                Bloom filter space used: 0
                Bloom filter off heap memory used: 0
                Index summary off heap memory used: 0
                Compression metadata off heap memory used: 0
                Compacted partition minimum bytes: 180
                Compacted partition maximum bytes: 258
                Compacted partition mean bytes: 258
                Average live cells per slice (last five minutes): NaN
                Maximum live cells per slice (last five minutes): 0
                Average tombstones per slice (last five minutes): NaN
                Maximum tombstones per slice (last five minutes): 0
                Dropped Mutations: 0
                Failed Replication Count: null

----------------
```
