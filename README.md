# Learning Cassandra

This document explains how to use docker with Cassandra and the exercises/solutions for the Datastax Foundations of Apache Cassandra

## Cassandra components

The main components used in a production environment are:

* **DataStax Enterprise**: Distribution of Apache Cassandra that includes Search, Analytics, Graph and Advanced Security.

* **DataStax Studio**: Interactive tool for developers. Can be used with CQL, DSE Graph and Gremlin Query Language.

* **DataStax Enterprise OpsCenter**: Web based management tool for DataStax.

## DataStax installation

This document is based on this tutorial: https://www.datastax.com/blog/2017/11/docker-tutorial

DataStax provides Docker images to be used in a non-production environment, to learn DataStax Enterprise, OpsCenter and DataStax Studio, to try new ideas, or to test and demonstrate an application. 

### Get the images

We start pulling the different images. Check in docker hub, the existing versions in case you need to use a different version: 

* dse-server: https://hub.docker.com/r/datastax/dse-server 
* dse-studio: https://hub.docker.com/r/datastax/dse-studio
* dse-opscenter: https://hub.docker.com/r/datastax/dse-opscenter

```
docker pull datastax/dse-server:6.7.0
docker pull datastax/dse-studio:6.7.0
docker pull datastax/dse-opscenter:6.7.0

docker volume create data-cassandra
docker volume create dse-analytics
docker volume create data-dsefs
docker volume create logs-cassandra
docker volume create logs-spark
```

### Running the containers

Description of the arguments. Note all of them are common arguments to run a container.

| Argument   |  Description |
|----------|:------|
| **-e** | Sets environment variables. Required to use: **DS_LICENSE=accept** |
| **-d** | Starts the container in the background |
| **-p** | Publish container ports on the host computer to allow remote access |
| **-v** | Mount a directory on the local host to preserve the container data |
| **-name** | Assigns a name to the container |

#### Starting the DSE Server

```
docker container run -e DS_LICENSE=accept --name my-dse --rm -d -p 9042:9042 -p 4040:4040 -p 7080:7080 -p 7081:7081 -p 8182:8182 -p 8983:8983 -p 8090:8090 -v data-cassandra:/var/lib/cassandra -v dse-analytics:/var/lib/spark -v data-dsefs:/var/lib/dsefs -v logs-cassandra:/var/log/cassandra -v logs-spark:/var/log/spark -v ${pwd}/assets201:/data-csv datastax/dse-server:6.7.0 
```

By default only runs the **dse-server**, to enable the rest of services we must provide any of the following arguments:

| Argument   |  Description |
|----------|:------|
| **-s** | Starts DSE Search |
| **-k** | Starts analytics |
| **-g** | Starts a DSE Graph |

Now will connect inside the container:

```
docker container exec -it my-dse bash
cqlsh
```

### Datastax Cluster

In order to run a Datastax cluster, there's a docker-compose.

```
docker-compose up -d
```

### Todo

Add the rest of reservices to the docker-compose
Test or add spark

### References

* Create Datastax Cluster: https://medium.com/@kayaerol84/cassandra-cluster-management-with-docker-compose-40265d9de076
* Reference Docker-compose: https://docs.docker.com/compose/compose-file/
* Docker Datastax Server: https://hub.docker.com/r/datastax/dse-server
* Ports Datastax: https://docs.datastax.com/en/security/6.7/security/secFirewallPorts.html#secFirewallPorts
* How to start all the Datastax services: https://docs.datastax.com/en/dse/6.0/dse-admin/datastax_enterprise/operations/startStop/startDseStandalone.html