# Replication

Cassandra stores replicas on multiple nodes to ensure reliability and fault tolerance. A replication strategy determines the nodes where replicas are placed. The total number of replicas across the cluster is referred to as the replication factor. A replication factor of 1 means that there is only one copy of each row in the cluster. If the node containing the row goes down, the row cannot be retrieved. A replication factor of 2 means two copies of each row, where each copy is on a different node. All replicas are equally important; there is no primary or master replica. As a general rule, the replication factor should not exceed the number of nodes in the cluster. However, you can increase the replication factor and then add the desired number of nodes later.

## Exercises

Note: Will start with the docker-compose created in the previous exercises. 

* For this exercise will have 2 Nodes in West and another 2 in East.
* Change the endpoint_snitch to GossipingPropertyFileSnitch


Using environment variables in the docker-compose we can build both DC.

**East side**

```
            - DC=east-side
            - RACK=hakuna-matata
            - SNITCH=GossipingPropertyFileSnitch
```

**West side**

```
            - DC=west-side
            - RACK=hakuna-matata
            - SNITCH=GossipingPropertyFileSnitch
```

Create a keyspace called killrvideo. With the following conditions:
* 2 replication in the west
* 2 in the east. 


1) Now we will re-import our KillVideo data. Open cqlsh. Execute the following CQL CREATE KEYSPACE statement to use NetworkTopologyStrategy with replication set to store one replica per data center:

First we check if the 2 DC are running:

```
nodetool status

Datacenter: east-side
=====================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address     Load       Tokens       Owns    Host ID                               Rack
UN  172.30.0.6  212.82 KiB  128          ?       9a0bb972-e3e0-4245-8ba5-92432414fccf  hakuna-matata
UN  172.30.0.5  131.66 KiB  128          ?       b3cd22de-80be-4d59-8ebf-cca40d84b37b  hakuna-matata
Datacenter: west-side
=====================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address     Load       Tokens       Owns    Host ID                               Rack
UN  172.30.0.2  218.52 KiB  128          ?       6a34a04e-d1f7-4a0c-901e-45bb8cc81348  hakuna-matata
UN  172.30.0.3  260.03 KiB  128          ?       25d849f0-0514-4af3-ae98-fed9c5d47a99  hakuna-matata
```

Once both Data Centers are running and all their nodes we can create the keyspace.

```
CREATE KEYSPACE killrvideo WITH replication = {'class': 'NetworkTopologyStrategy','east-side': 2,'west-side': 2};
```

Check that the Keyspace exists.

```
desc keyspaces;
```

2. Switch to the new keyspace and create a table videos_by_tag  sorted by added_Date desc with the following structure:


| Field Name | Datatype | Primary key |
| --- | ----------- | ----------- |
| tag | Text | Yes | 
| video_id | uuid | Clust |
| added_date | timestamp | Clust |
| title | text | |

```
use killrvideo;


CREATE TABLE videos_by_tag (tag text,video_id uuid,added_date timestamp,title text,PRIMARY KEY ((tag), added_date, video_id)) WITH CLUSTERING ORDER BY (added_date DESC);
```

3. Ingest the file `videos-by-tag.csv`

```
docker container exec dse6_west_side_node1 mkdir data-csv
docker cp ../assets201/videos-by-tag.csv dse6_west_side_node1:/opt/dse/data-csv

COPY videos_by_tag(tag, video_id, added_date, title) FROM '/opt/dse/data-csv/videos-by-tag.csv' WITH HEADER=TRUE;
```

4. Check in the 2 DC if the data have been replicated.

* Run in DC west-side

```
SELECT * FROM videos_by_tag;
SELECT * FROM videos_by_tag WHERE tag = 'cassandra';
SELECT * FROM videos_by_tag WHERE tag = 'datastax';
```

* Run in DC east-side

```
SELECT * FROM videos_by_tag;
SELECT * FROM videos_by_tag WHERE tag = 'cassandra';
SELECT * FROM videos_by_tag WHERE tag = 'datastax';
```

5. Let's determine which nodes our replicas ended up on. Execute the following commands in the terminal:

```
nodetool getendpoints killrvideo videos_by_tag 'cassandra'

172.30.0.2
172.30.0.5
172.30.0.3
172.30.0.6

nodetool getendpoints killrvideo videos_by_tag 'datastax'

172.30.0.2
172.30.0.5
172.30.0.3
172.30.0.6
```

It returns all the ips that contains this key.