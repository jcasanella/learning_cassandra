# Write Path

## Exercises

1. Investigate the commit-log directory.

```
ls /var/lib/cassandra/commitlog
CommitLog-600-1588417410644.log  CommitLog-600-1588417410645.log
```

2. Let's put a watch on this directory to see how it changes as we write data to Apache Cassandra. Do that from another terminal. And run the tool to stress Cassandra adding 250000 rows.

```
watch -n 1 -d "ls -lh /var/lib/cassandra/commitlog"
/opt/dse/resources/cassandra/tools/bin/cassandra-stress write no-warmup n=250000 -port native=9042 -rate threads=1
```

Be sure your second terminal is also visible as cassandra-stress executes. cassandra-stress will write 250,000 rows to your node.

There are a few things to watch out for while cassandra-stress inserts keys:

* The total size will continue to increase.
* The timestamp will change for the current segment being written.
* You may get additional commit log files as well.

Different outputs from watch: (CommitLog folder)

```
Every 1.0s: ls -lh /var/lib/cassandra/commitlog                                                                                                  node1: Sat May  2 12:33:58 2020
total 17M
-rw-r--r-- 1 dse dse 17M May  2 12:33 CommitLog-600-1588417410644.log
-rw-r--r-- 1 dse dse  20 May  2 11:03 CommitLog-600-1588417410645.log

Every 1.0s: ls -lh /var/lib/cassandra/commitlog                                                                                                  node1: Sat May  2 12:34:44 2020
total 18M
-rw-r--r-- 1 dse dse 18M May  2 12:34 CommitLog-600-1588417410644.log
-rw-r--r-- 1 dse dse  20 May  2 11:03 CommitLog-600-1588417410645.log
```

Execute the following nodetool command: `nodetool cfstats keyspace1.standard1` See below the output:

```
Total number of tables: 53
----------------
Keyspace : keyspace1
        Read Count: 0
        Read Latency: NaN ms
        Write Count: 71105
        Write Latency: 0.020221840939455733 ms
        Pending Flushes: 0
                Table: standard1
                SSTable count: 0
                Space used (live): 0
                Space used (total): 0
                Space used by snapshots (total): 0
                Off heap memory used (total): 20833765
                SSTable Compression Ratio: -1.0
                Number of partitions (estimate): 71104
                Memtable cell count: 71105
                Memtable data size: 19838295
                Memtable off heap memory used: 20833765
                Memtable switch count: 0
                Local read count: 0
                Local read latency: NaN ms
                Local write count: 71105
                Local write latency: 0.019 ms
                Pending flushes: 0
                Percent repaired: 100.0
                Bytes repaired: 0.000KiB
                Bytes unrepaired: 0.000KiB
                Bytes pending repair: 0.000KiB
                Bloom filter false positives: 0
                Bloom filter false ratio: 0.00000
                Bloom filter space used: 0
                Bloom filter off heap memory used: 0
                Index summary off heap memory used: 0
                Compression metadata off heap memory used: 0
                Compacted partition minimum bytes: 0
                Compacted partition maximum bytes: 0
                Compacted partition mean bytes: 0
                Average live cells per slice (last five minutes): NaN
                Maximum live cells per slice (last five minutes): 0
                Average tombstones per slice (last five minutes): NaN
                Maximum tombstones per slice (last five minutes): 0
                Dropped Mutations: 0
                Failed Replication Count: null

----------------
```

Important are the memtable details from the previous output.

```
Memtable cell count: 71105
Memtable data size: 19838295
Memtable off heap memory used: 20833765
Memtable switch count: 0
```

2. Flush the memtable, run again the command to get this statistics and compare the memtable values.

```
nodetool flush
nodetool cfstats keyspace1.standard1

Total number of tables: 53
----------------
Keyspace : keyspace1
        Read Count: 0
        Read Latency: NaN ms
        Write Count: 71105
        Write Latency: 0.020221840939455733 ms
        Pending Flushes: 0
                Table: standard1
                SSTable count: 1
                Space used (live): 17004130
                Space used (total): 17004130
                Space used by snapshots (total): 0
                Off heap memory used (total): 88888
                SSTable Compression Ratio: -1.0
                Number of partitions (estimate): 70730
                Memtable cell count: 0
                Memtable data size: 0
                Memtable off heap memory used: 0
                Memtable switch count: 1
                Local read count: 0
                Local read latency: NaN ms
                Local write count: 71105
                Local write latency: 0.019 ms
                Pending flushes: 0
                Percent repaired: 0.0
                Bytes repaired: 0.000KiB
                Bytes unrepaired: 15.527MiB
                Bytes pending repair: 0.000KiB
                Bloom filter false positives: 0
                Bloom filter false ratio: 0.00000
                Bloom filter space used: 88896
                Bloom filter off heap memory used: 88888
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
```

Note the memtable statistics zeroed out because we flushed the previous memtable to disk.