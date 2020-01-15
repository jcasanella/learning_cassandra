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
docker container run -e DS_LICENSE=accept --name my-dse -d datastax/dse-server:6.7.0
```

By default only runs the **dse-server**, to enable the rest of services we must provide any of the following arguments.
