# Gossip Protocol

In Cassandra, all nodes are the same and have peer to peer architecture and there is no concept of a master-slave node. In Cassandra all nodes communicating with each other via a gossip protocol. Gossip is the message system that Cassandra node use to make their data consistent with each other.

## What is Gossip protocol?

Gossip is the message system that Cassandra nodes, virtual nodes used to make their data consistent with each other, and is used to enforce the replication factor in a cluster.

The Gossip protocol in Cassandra have three phases.**SYN**, **ACK**, **ACK2**. Node A sends a `SYN` message to Node B. After receiving the `SYN` message, Node B replies with an `ACK` message. After receiving the `ACK` message from Node B, Node A replies with `ACK2` message to Node B to complete the message exchange.

One node needless to talk with all nodes to know something about them. To avoid the communication chaos when one node talks to another node it not only provides information about its status. However, it also provides the latest information about the nodes that it had communicated with before.

The gossip protocol is also used to failure detection it behaves very much like TCP protocol, trying to get an acknowledge response before considering whether a Node is up or down. Essentially, when two nodes communicate with one another; for instance when Node 1 sends a SYN message (similarly to the TCP protocol) to the Node 2, it expects to receive an ACK message back and then send again an ACK message to Node 2 confirming the 3 way handshake. If the Node 2 does not reply to the SYN message, it is marked as down. Even when the nodes are down, the other nodes will be periodically pinging and that is how the failure detection happens.

## Exercises

1. Execute the following command: `nodetool gossipinfo`. See the gossip information.

```
/172.30.0.4
  generation:1587223307
  heartbeat:289
  STATUS:51:NORMAL,-4319372312230471416
  LOAD:285:224279.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.4
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.4
  DSE_GOSSIP_STATE:197:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-04","graph":false,"health":0.3}
  NET_VERSION:1:512
  HOST_ID:2:93e2f0eb-e71b-4d0d-940f-bd6dd9453e30
  NATIVE_TRANSPORT_READY:70:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
/172.30.0.2
  generation:1587223311
  heartbeat:283
  STATUS:51:NORMAL,-3316946433367826278
  LOAD:222:286973.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.2
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.2
  DSE_GOSSIP_STATE:196:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-02","graph":false,"health":0.3}
  NET_VERSION:1:512
  HOST_ID:2:7709d81d-f2d4-4a0b-a782-a3bf0a217808
  NATIVE_TRANSPORT_READY:68:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
/172.30.0.3
  generation:1587223307
  heartbeat:289
  STATUS:51:NORMAL,-1028404527652642016
  LOAD:285:263337.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.3
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.3
  DSE_GOSSIP_STATE:197:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-03","graph":false,"health":0.3}
  NET_VERSION:1:512
  HOST_ID:2:f4b2302a-861d-4eb5-aa0d-aa7cc330a2e0
  NATIVE_TRANSPORT_READY:68:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
```

**Note**: if you run `nodetool gossipinfo` a few times notice the heartbeat values increasing for all nodes. 

1. Terminate one of the nodes and run again the `nodetool gossipinfo` in one of the running nodes, check what is the status of the node stopped.

```
nodetool stopdaemon
```

Running from a running node:

```
nodetool gossipinfo

/172.30.0.4
  generation:1587223307
  heartbeat:2880
  STATUS:51:NORMAL,-4319372312230471416
  LOAD:2875:224279.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.4
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.4
  DSE_GOSSIP_STATE:1840:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-04","graph":false,"health":0.8}
  NET_VERSION:1:512
  HOST_ID:2:93e2f0eb-e71b-4d0d-940f-bd6dd9453e30
  NATIVE_TRANSPORT_READY:70:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
/172.30.0.2
  generation:1587223311
  heartbeat:2876
  STATUS:51:NORMAL,-3316946433367826278
  LOAD:2875:286973.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.2
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.2
  DSE_GOSSIP_STATE:1838:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-02","graph":false,"health":0.8}
  NET_VERSION:1:512
  HOST_ID:2:7709d81d-f2d4-4a0b-a782-a3bf0a217808
  NATIVE_TRANSPORT_READY:68:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
/172.30.0.3
  generation:1587223307
  heartbeat:2147483647
  STATUS:2597:shutdown,true
  LOAD:2558:263337.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.3
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.3
  DSE_GOSSIP_STATE:2594:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"false","server_id":"02-42-AC-1E-00-03","graph":false,"health":0.8}
  NET_VERSION:1:512
  HOST_ID:2:f4b2302a-861d-4eb5-aa0d-aa7cc330a2e0
  NATIVE_TRANSPORT_READY:2598:false
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
```

We can appreciate the status of the last one: `STATUS:2597:shutdown,true`
Now restart the node that is run and check again the status:

We're assuing the node that is down is node2 and running from our docker-compose.

```
docker-compose start node2
```

Once is up, we can check the gossipinfo

```
nodetool gossipinfo

/172.30.0.4
  generation:1587223307
  heartbeat:3397
  STATUS:51:NORMAL,-4319372312230471416
  LOAD:3379:230498.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.4
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.4
  DSE_GOSSIP_STATE:1840:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-04","graph":false,"health":0.8}
  NET_VERSION:1:512
  HOST_ID:2:93e2f0eb-e71b-4d0d-940f-bd6dd9453e30
  NATIVE_TRANSPORT_READY:70:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
/172.30.0.2
  generation:1587223311
  heartbeat:3393
  STATUS:51:NORMAL,-3316946433367826278
  LOAD:3379:293071.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.2
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.2
  DSE_GOSSIP_STATE:1838:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-02","graph":false,"health":0.8}
  NET_VERSION:1:512
  HOST_ID:2:7709d81d-f2d4-4a0b-a782-a3bf0a217808
  NATIVE_TRANSPORT_READY:68:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
/172.30.0.3
  generation:1587226451
  heartbeat:86
  STATUS:51:NORMAL,-1028404527652642016
  LOAD:24:328581.0
  SCHEMA:20:edd41673-edd5-38b8-9485-b8dd05832e03
  DC:16:DC1
  RACK:18:rack1
  RELEASE_VERSION:4:4.0.0.670
  INTERNAL_IP:14:172.30.0.3
  NATIVE_TRANSPORT_ADDRESS:3:172.30.0.3
  DSE_GOSSIP_STATE:69:{"dse_version":"6.7.0","workloads":"Cassandra","workload":"Cassandra","active":"true","server_id":"02-42-AC-1E-00-03","graph":false}
  NET_VERSION:1:512
  HOST_ID:2:f4b2302a-861d-4eb5-aa0d-aa7cc330a2e0
  NATIVE_TRANSPORT_READY:66:true
  NATIVE_TRANSPORT_PORT:7:9042
  NATIVE_TRANSPORT_PORT_SSL:8:9042
  STORAGE_PORT:9:7000
  STORAGE_PORT_SSL:10:7001
  JMX_PORT:11:7199
  SCHEMA_COMPATIBILITY_VERSION:5:1
  TOKENS:50:<hidden>
```