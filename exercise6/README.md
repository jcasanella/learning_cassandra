# Ring

1. Stop a node

```
nodetool status

Datacenter: DC1
===============
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address     Load       Tokens       Owns    Host ID                               Rack
UN  172.30.0.4  330.98 KiB  3            ?       93e2f0eb-e71b-4d0d-940f-bd6dd9453e30  rack1
UN  172.30.0.2  244.8 KiB  3            ?       7709d81d-f2d4-4a0b-a782-a3bf0a217808  rack1
UN  172.30.0.3  319.03 KiB  3            ?       f4b2302a-861d-4eb5-aa0d-aa7cc330a2e0  rack1

Note: Non-system keyspaces don't have the same replication settings, effective ownership information is meaningless

nodetool stopdaemon
```

2. Connect to another node and check which nodes are running in the ring.

```
nodetool ring

Datacenter: DC1
==========
Address     Rack        Status State   Load            Owns                Token
                                                                           7052642274492871044
172.30.0.2  rack1       Down   Normal  244.8 KiB       ?                   -8864404327061766114
172.30.0.4  rack1       Up     Normal  330.98 KiB      ?                   -7462868571315231413
172.30.0.3  rack1       Up     Normal  319.03 KiB      ?                   -7114053154637899502
172.30.0.4  rack1       Up     Normal  330.98 KiB      ?                   -4319372312230471416
172.30.0.2  rack1       Down   Normal  244.8 KiB       ?                   -3316946433367826278
172.30.0.3  rack1       Up     Normal  319.03 KiB      ?                   -1028404527652642016
172.30.0.4  rack1       Up     Normal  330.98 KiB      ?                   4343947691487864982
172.30.0.3  rack1       Up     Normal  319.03 KiB      ?                   5327833370551862773
172.30.0.2  rack1       Down   Normal  244.8 KiB       ?                   7052642274492871044

  Warning: "nodetool ring" is used to output all the tokens of a node.
  To view status related info of a node use "nodetool status" instead.


  Note: Non-system keyspaces don't have the same replication settings, effective ownership information is meaningless
```

3. `getendpoints` returns the IP addresses of the node(s) which store the partitions with the respective partition key value (the last argument in single quotes: cassandra and datastax respectively).

```
nodetool getendpoints killrvideo videos_by_tag 'cassandra'

172.30.0.3
172.30.0.4
172.30.0.2

nodetool getendpoints killrvideo videos_by_tag 'datastax'

172.30.0.3
172.30.0.4
172.30.0.2
```