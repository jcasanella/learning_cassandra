# Snitches

To improve fault-tolerance, distributed systems must consider the topology of the cluster into account. Apache Cassandra™ uses this topological information to replicate data across a geological diverse area - which increases data availability.

## Overview

In Cassandra Snitch is very useful and snitch is also helps in keep record to avoid storing multiple replicas of data on the same rack. In Cassandra, it is very important aspects to avoid multiple replica. In replication strategy we assign number of replica and also we define the data-center. This information is very helpful for snitch to identify the node and which rack belong to.

In Cassandra, snitch job is to determine which data centers and racks it should use to read data from and write data to. In Cassandra, all snitch are dynamic by default.

## Exercises

In this exercise will build a cluster with 4 four nodes:

* 2 nodes in `dc=west-side` and `rack=hakuna-matata`
* 2 nodes in `dc=east-side` and `rack=hakuna-matata`

In order to be able to use this thopology we must change the snith type to `endpoint_snitch: GossipingPropertyFileSnitch`.

This change should be done in the `cassandra.yml`, however because we're using a docker-compose, we can apply this change using an environment variable. 

```
            - SNITCH=GossipingPropertyFileSnitch
```

We must edit the following file: `cassandra-rackdc.properties` in a real environment for every node. Will have 2 different files:

**File1**:

```
# These properties are used with GossipingPropertyFileSnitch and will
# indicate the rack and dc for this node
dc=west-side
rack=hakuna-matata
```

**File2**:

```
# These properties are used with GossipingPropertyFileSnitch and will
# indicate the rack and dc for this node
dc=east-side
rack=hakuna-matata
```

However we can do this change from the docker-compose, again using environment variables, see this snippet for 2 different nodes:

**East node**:

```
            - DC=east-side
            - RACK=hakuna-matata
```

**West node**:

```
            - DC=west-side
            - RACK=hakuna-matata
```

Start up all the nodes and run the following command in the different data center: 

**West Data Center**

```
docker container exec -it node1-west bash

bin/nodetool status

DC: west-side       Workload: Cassandra       Graph: no
======================================================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--   Address          Load             Owns                 VNodes                                       Rack         Health [0,1]
UN   172.30.0.2       130.81 KiB       ?                    3                                            hakuna-matata 0.00
UN   172.30.0.3       112.14 KiB       ?                    3                                            hakuna-matata 0.00

Note: you must specify a keyspace to get ownership information.
```

**East Data Center**:

```
docker container exec -it node1-east bash

bin/nodetool status

DC: east-side       Workload: Cassandra       Graph: no
======================================================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--   Address          Load             Owns                 VNodes                                       Rack         Health [0,1]
UN   172.30.0.4       130.83 KiB       ?                    3                                            hakuna-matata 0.20
UN   172.30.0.5       102.14 KiB       ?                    3                                            hakuna-matata 0.00

Note: you must specify a keyspace to get ownership information.
```
