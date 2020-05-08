# User Defined Types (UDTs)

After reviewing your design of the tables that support tag and year queries, you realised is missing a column to store video metadata and another one for the tags. Change the schemas to add the metadata video, like in the following schemas:

| Column Name | Data Type |
| ----------- | --------- |
| video_id | timeuuid |
| added_date | timestamp |
| description | text |
| encoding | video_encoding |
| tags |  set < text > |
| title	| text |
| user_id | uuid |

The encoding data structure:

| Field Name | Data Type |
| ---------- | --------- |
| bit_rates	| set < text > |
| encoding | text |
| height | int |
| width | int |

1. Run the TRUNCATE command to erase the data from the videos table and add to the videos table the tags column. Before to add the tags column check the structure of the csv file to be loaded (assets220/labwork/udts/videos.csv). Once you get all the info apply all the required changes, load the data using the copy command.

```
The structure of the csv file is: video id, added date, description, tags, title, user_id

USE killrvideo;
DESC videos;

This is the structure of the videos table:
    video_id timeuuid PRIMARY KEY,
    added_date timestamp,
    description text,
    title text,
    user_id uuid

ALTER TABLE videos ADD tags SET<TEXT>;

docker cp assets220/ds220-6.0-labwork/labwork/udts/videos.csv dse6_node1:/.

USE killrvideo;

COPY videos FROM '/videos.csv' WITH HEADER=true;

SELECT count(*) FROM videos;
```

1. We do not need to create the User Defined Type called video_encoding because we did in the previous exercise. Add the new data type to videos table. Once the column is added load the data from the `videos_encoding.csv` file using the COPY command. Run some queries to see the new cols added to the table.

```
DESCRIBE TYPE video_encoding;

CREATE TYPE killrvideo.video_encoding (
    encoding text,
    height int,
    width int,
    bit_rates set<text>
);

ALTER TABLE videos ADD encoding FROZEN<video_encoding>;

docker cp assets220/ds220-6.0-labwork/labwork/udts/videos_encoding.csv dse6_node1:/.

USE killrvideo;

COPY videos (video_id, encoding) FROM '/videos_encoding.csv' WITH HEADER=true;

SELECT * FROM videos LIMIT 20;
SELECT tags, encoding FROM videos LIMIT 20;
```