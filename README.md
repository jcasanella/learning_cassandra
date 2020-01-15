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

Lets run some CQL: (Note: the following queries are extracted from https://dzone.com/articles/cassandra-data-modeling-primary-clustering-partiti)

**Create a new schema**

A schema is similar to the database concept in a traditional database.

```
create keyspace Student_Details with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

use student_details;
```

Now, it's time to create a table and insert some data.

```
-- Create the table
CREATE TABLE student(stuid int, avg_marks float, description text, PRIMARY KEY(stuid));

-- show the existing tables in the schema
DESC TABLES;

-- insert some data
 INSERT INTO student (stuid, avg_marks, description) VALUES (1, 25.5, 'student 1');
INSERT INTO student (stuid, avg_marks, description) VALUES (2, 35.5, 'student 2');

-- check the content of the table
SELECT * FROM student;

-- more info of the query to run
EXPAND

-- to see the token created when row inserted
SELECT token(stuid) FROM student;

SELECT * FROM student WHERE stuid = 1; 
```

Create another table:

```
-- Table with partition key (stuid) and cluster key (exam_date)
CREATE TABLE marks(stuid int,exam_date timestamp,marks float, exam_name text, PRIMARY KEY (stuid,exam_date));

-- Insert some data
INSERT INTO marks(stuid ,exam_date ,marks ,exam_name) VALUES (1,'2016-11-10',76 ,'examA');
INSERT INTO marks(stuid ,exam_date ,marks ,exam_name) VALUES (1,'2016-11-11',90 ,'examB');
INSERT INTO marks(stuid ,exam_date ,marks ,exam_name) VALUES (1,'2016-11-12',68 ,'examC');
```
