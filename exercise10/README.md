# Consistency Levels

1. Check consistency level of the keyspace `killrvideo` and change the consistency level to 2.

```
USE killrvideo;
CONSISTENCY;

Current consistency level is ONE.

CONSISTENCY TWO;
CONSISTENCY;

Current consistency level is TWO.
```

2. etrieve the cassandra partition from the videos_by_tag

```
SELECT * FROM videos_by_tag WHERE tag = 'cassandra';
```

3. Determine which nodes hold the replicas for the cassandra partition tag value in the videos_by_tag table

```
nodetool getendpoints killrvideo videos_by_tag 'cassandra'
```