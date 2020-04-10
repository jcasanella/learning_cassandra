# What is a node

## Node information

The `status` command shows information about the entire cluster, particularly the state of each node, and information about each of those nodes: IP address, data load, number of tokens, total percentage of data saved on each node, host ID, and datacenter and rack. We will discuss these in detail as the course progresses. 

```
nodetool status

Datacenter: dc1
===============
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address     Load       Owns    Host ID                               Token                                    Rack
UN  172.17.0.2  229.43 KiB  ?       1df80cd7-62c6-4998-a017-b72b6aa181c0  269992358218349327                       rack1
Note: Non-system keyspaces don't have the same replication settings, effective ownership information is meaningless
```

Take note as to the differences between ```dsetool status``` and ```nodetool status```. Although both tools have a status command, dsetool works with DataStax Enterprise™ as a whole (Apache Cassandra™, Apache Spark™, Apache Solr™, Graph) whereas nodetool is specific to Apache Cassandra™. Their functionality diverges from here.

```
DC: dc1             Workload: Cassandra       Graph: no
======================================================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--   Address          Load             Owns                 Token                                        Rack         Health [0,1]
UN   172.17.0.2       229.43 KiB       ?                    269992358218349327                           rack1        1.00

Note: you must specify a keyspace to get ownership information.
```

The `info` command displays information about the connected node, which includes token information, host ID, protocol status, data load, node uptime, heap memory usage and capacity, datacenter and rack information, number of errors reported, cache usage, and percentage of SSTables that have been incrementally repaired. 

```
nodetool info

ID                     : 1df80cd7-62c6-4998-a017-b72b6aa181c0
Gossip active          : true
Native Transport active: true
Load                   : 229.43 KiB
Generation No          : 1586379836
Uptime (seconds)       : 61752
Heap Memory (MB)       : 435.85 / 1992.00
Off Heap Memory (MB)   : 0.13
Data Center            : dc1
Rack                   : rack1
Exceptions             : 0
Key Cache              : entries 0, size 0 bytes, capacity 99 MiB, 0 hits, 0 requests, NaN recent hit rate, 14400 save period in seconds
Row Cache              : entries 0, size 0 bytes, capacity 0 bytes, 0 hits, 0 requests, NaN recent hit rate, 0 save period in seconds
Counter Cache          : entries 0, size 0 bytes, capacity 49 MiB, 0 hits, 0 requests, NaN recent hit rate, 7200 save period in seconds
Chunk Cache            : entries 262, size 8.35 MiB, capacity 1.39 GiB, 262 misses, 90195 requests, 0.997 recent hit rate, 491.520 microseconds miss latency
Percent Repaired       : 100.0%
Token                  : 269992358218349327
```

`describecluster` shows the settings that are common across all of the nodes in the cluster and the current schema version used by each node.

```
nodetool describecluster

Cluster Information:
        Name: Test Cluster
        Snitch: org.apache.cassandra.locator.GossipingPropertyFileSnitch
        DynamicEndPointSnitch: enabled
        Partitioner: org.apache.cassandra.dht.Murmur3Partitioner
        Schema versions:
                cbc3b9b5-1f6b-32e3-8a5d-5e20666e4ed0: [172.17.0.2]
```

## Node logging

The command `setlogginglevel` dynamically changes the logging level used by ApacheCassandra™ without the need for a restart. You can also look at the **/var/log/cassandra/system.log** afterwards to observe the changes.

```
nodetool getlogginglevels

Logger Name                                        Log Level
ROOT                                                    INFO
DroppedAuditEventLogger                                 INFO
SLF4JAuditWriter                                        INFO
com.cryptsoft                                            OFF
com.datastax.bdp.db                                    DEBUG
com.datastax.bdp.search.solr.metrics.SolrMetricsEventListener     DEBUG
com.datastax.bdp.util.process.InternalServiceRunner     DEBUG
com.datastax.bdp.util.process.ServiceRunner            DEBUG
com.datastax.driver.core.NettyUtil                     ERROR
org.apache.cassandra                                   DEBUG
org.apache.lucene.index                                 INFO
org.apache.solr.core.CassandraSolrConfig                WARN
org.apache.solr.core.RequestHandlers                    WARN
org.apache.solr.core.SolrCore                           WARN
org.apache.solr.handler.component                       WARN
org.apache.solr.search.SolrIndexSearcher                WARN
org.apache.solr.update                                  WARN
org.apache.spark.rpc                                   ERROR

nodetool setlogginglevel org.apache.cassandra TRACE

nodetool getlogginglevels

Logger Name                                        Log Level
ROOT                                                    INFO
DroppedAuditEventLogger                                 INFO
SLF4JAuditWriter                                        INFO
com.cryptsoft                                            OFF
com.datastax.bdp.db                                    DEBUG
com.datastax.bdp.search.solr.metrics.SolrMetricsEventListener     DEBUG
com.datastax.bdp.util.process.InternalServiceRunner     DEBUG
com.datastax.bdp.util.process.ServiceRunner            DEBUG
com.datastax.driver.core.NettyUtil                     ERROR
org.apache.cassandra                                   TRACE
org.apache.lucene.index                                 INFO
org.apache.solr.core.CassandraSolrConfig                WARN
org.apache.solr.core.RequestHandlers                    WARN
org.apache.solr.core.SolrCore                           WARN
org.apache.solr.handler.component                       WARN
org.apache.solr.search.SolrIndexSearcher                WARN
org.apache.solr.update                                  WARN
org.apache.spark.rpc                                   ERROR
```

## Stop Start Cassandra

The `drain` command stops writes from occurring on the node and flushes all data to disk. Typically, this command may be run before stopping an Apache Cassandra™ node.

```
nodetool drain
```

The `stopdaemon` command stops a node's execution. Wait for it to complete.

```
nodetool stopdaemon
```

Restart your node by running:

```
dse cassandra
```

The `flush` command commits all written (memtable, discussed later) data to disk. Unlike drain, flush allows further writes to occur.

```
nodetool flush
```