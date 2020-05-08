# Using counters in CQL

## Overview

In this exercise, you will:

* Create a new table that makes use of the counter type
* Load the newly created table with data
* Run queries against the table to test counter functionality

A counter is a special column used to store an integer that is changed in increments. Counters are useful for many data models. Some examples:

* To keep track of the number of web page views received on a company website
* To keep track of the number of games played online or the number of players who have joined an online game

Because counters are implemented differently from other columns, counter columns can only be created in dedicated tables. A counter column must have the datatype counter data type. This data type cannot be assigned to a column that serves as the primary key or partition key. To implement a counter column, create a table that only includes:

* The primary key (can be one or more columns)
* The counter column

A counter column cannot be indexed or deleted.. To load data into a counter column, or to increase or decrease the value of the counter, use the UPDATE command.

## Exercises

1. Create a new table called videos_count_by_tag with a column video_count which makes use of a counter type to store the video count. Structure your table to work correctly with the CQL in `assets220/ds220-6.0-labwork/labwork/counters/videos_count_by_tag.cql`

```
Line example from the file: 
UPDATE videos_count_by_tag SET video_count = video_count + 3 WHERE tag = 'dse' AND added_year = 2015;

USE killrvideo;

CREATE TABLE videos_count_by_tag(
    tag TEXT,
    added_year INT,
    video_count COUNTER,
    PRIMARY KEY(tag, added_year)
);
```

2. Load the number of counts from the videos_count_by_tag.cql file into the videos_count_by_tag table using the SOURCE command.

```
docker cp assets220/ds220-6.0-labwork/labwork/counters/videos_count_by_tag.cql dse6_node1:/.

USE killrvideo;

SOURCE '/videos_count_by_tag.cql'

SELECT * FROM videos_count_by_tag LIMIT 10;
```

3. Simulate adding another a tag for another video by incrementing the video count for a category, and then querying the new count from the videos_count_by_tag table.

```
SELECT * FROM videos_count_by_tag WHERE tag='node.js' AND added_year=2015;
UPDATE videos_count_by_tag SET video_count=video_count+1 WHERE tag='node.js' AND added_year=2015;
SELECT * FROM videos_count_by_tag WHERE tag='node.js' AND added_year=2015;
```