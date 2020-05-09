# Physical Data Modeling

## Exercises

1. Use a text editor to open and review the killrvideo.cql file in the `assets220/ds220-6.0-labwork/labwork/final` directory.
There are several tables with columns marked with *CQL Type*. Fill in the appropriate data type for the columns in users_by_email, users, videos_by_user, and comments_by_user.
Run the SOURCE command on the killrvideo.cql file to execute the CREATE TABLE statement.

File aftter changes: ( `assets220/ds220-6.0-labwork/labwork/final/killrvideo_answer.cql')

```
DROP KEYSPACE IF EXISTS killr_video;

CREATE KEYSPACE killr_video WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE killr_video;

CREATE TABLE users_by_email (
   email TEXT,
   password TEXT,
   user_id UUID,
   PRIMARY KEY ((email))
);

CREATE TABLE users (
   user_id UUID,
   email TEXT,
   first_name TEXT,
   last_name TEXT,
   registration_date TIMESTAMP,
   PRIMARY KEY((user_id))
);

CREATE TABLE videos_by_user (
   user_id UUID,
   video_id UUID,
   title TEXT,
   type TEXT,
   tags SET<TEXT>,
   preview_thumbnails MAP<INT, BLOB>,
   PRIMARY KEY ((user_id), video_id))
WITH CLUSTERING ORDER BY (video_id DESC);

CREATE TABLE comments_by_user (
   user_id UUID,
   posted_timestamp TIMESTAMP,
   video_id TIMEUUID,
   comment TEXT,
   title TEXT,
   type TEXT,
   tags SET<TEXT>,
   preview_thumbnails MAP<INT, BLOB>,
   PRIMARY KEY ((user_id), posted_timestamp, video_id))
WITH CLUSTERING ORDER BY (posted_timestamp DESC, video_id ASC);

CREATE TABLE comments_by_video (
   video_id TIMEUUID,
   posted_timestamp TIMESTAMP,
   user_id UUID,
   comment TEXT,
   title TEXT STATIC,
   type TEXT STATIC,
   tags SET<TEXT> STATIC,
   preview_thumbnails MAP<INT,BLOB> STATIC,
   PRIMARY KEY ((video_id), posted_timestamp, user_id))
WITH CLUSTERING ORDER BY (posted_timestamp DESC, user_id ASC);

CREATE TABLE latest_videos (
   video_bucket INT,
   video_id TIMEUUID,
   title TEXT,
   type TEXT,
   tags SET<TEXT>,
   preview_thumbnails MAP<INT,BLOB>,
   PRIMARY KEY ((video_bucket), video_id))
WITH CLUSTERING ORDER BY (video_id DESC);

CREATE TABLE ratings_by_video (
   video_id TIMEUUID,
   num_ratings COUNTER,
   sum_ratings COUNTER,
   PRIMARY KEY ((video_id))
);

CREATE TYPE encoding_type (
   encoding TEXT,
   height INT,
   width INT,
   bit_rates SET<TEXT>
);

CREATE TABLE videos (
   video_id TIMEUUID,
   user_id UUID,
   title TEXT,
   description TEXT,
   type TEXT,
   url TEXT,
   release_date TIMESTAMP,
   avg_rating FLOAT,
   mpaa_rating TEXT,
   encoding FROZEN<encoding_type>,
   tags SET<TEXT>,
   preview_thumbnails MAP<INT,BLOB>,
   genres SET<TEXT>,
   PRIMARY KEY ((video_id))
);

CREATE TABLE trailers_by_video (
   video_id TIMEUUID,
   title TEXT,
   trailer_id TIMEUUID,
   type TEXT,
   tags SET<TEXT>,
   preview_thumbnails MAP<INT,BLOB>,
   PRIMARY KEY ((video_id), title, trailer_id)
);

CREATE TABLE actors_by_video (
   video_id TIMEUUID,
   actor_name TEXT,
   character_name TEXT,
   PRIMARY KEY ((video_id), actor_name, character_name)
);

CREATE TABLE video_interactions_by_user_video (
   user_id UUID,
   video_id TIMEUUID,
   event_timestamp TIMESTAMP,
   event_type TEXT,
   video_timestamp TIMESTAMP,
   PRIMARY KEY ((user_id, video_id), event_timestamp))
WITH CLUSTERING ORDER BY (event_timestamp DESC);

docker cp assets220/ds220-6.0-labwork/labwork/final/killrvideo_answer.cql dse6_node1:/.

SOURCE '/killrvideo_answer.cql'
```

2. If the SOURCE command was successful, you should now have a new keyspace called killr_video. Run the `DESCRIBE KEYSPACE` command on the killr_video keyspace to review the keyspace and table schema. Change to the ney keyspace and load the data for the following tables:

* videos
* latest_videos
* trailers_by_video
* actors_by_video

```
docker cp assets220/ds220-6.0-labwork/labwork/final/actors_by_video.csv dse6_node1:/.    
docker cp assets220/ds220-6.0-labwork/labwork/final/trailers_by_video.csv dse6_node1:/.
docker cp assets220/ds220-6.0-labwork/labwork/final/latest_videos.csv dse6_node1:/.
docker cp assets220/ds220-6.0-labwork/labwork/final/videos.csv dse6_node1:/. 

DESC KEYSPACE killr_video;

USE killr_video;

COPY videos FROM '/videos.csv' WITH HEADER=true;
SELECT count(*) FROM videos;

COPY latest_videos FROM '/latest_videos.csv' WITH HEADER=true;
SELECT count(*) FROM latest_videos;

COPY trailers_by_video FROM '/trailers_by_video.csv' WITH HEADER=true;
SELECT count(*) FROM trailers_by_video;

COPY actors_by_video FROM '/actors_by_video.csv' WITH HEADER=true;
SELECT count(*) FROM actors_by_video;
```

3. Query the latest_videos table to find the most recent 50 videos that was uploaded. Is there a video uploaded for the movie Gone Girl? What is the video_id for that movie?

```
SELECT * FROM latest_videos LIMIT 50;

Yes, there's one.
0 | 8a657435-0ef2-11e5-91b1-8438355b7e3a |               null | null |                                                   Gone Girl |   Movie
```

4. Let's find out some more information about this movie. Query the videos table using the previously found video_id. When was this movie released? What are the genres for this movie?

```
SELECT release_date, genres FROM videos WHERE video_id=8a657435-0ef2-11e5-91b1-8438355b7e3a;

Movie released: 2014-09-26 00:00:00.000000+0000
Genres: {'Drama', 'Mystery', 'Thriller'}
```

5. We can also find the actors that were in the movie and the characters they played. Go ahead and query the actors_by_video table using the video_id for Gone Girl. Who was the actor that played the character Desi Collings?

```
SELECT * FROM actors_by_video WHERE video_id=8a657435-0ef2-11e5-91b1-8438355b7e3a;
The actor is Neil Patrick Harris
```

1. Query the trailers_by_video table to check if there are any trailers available for this movie. If there is a trailer available, make note of the trailer_id and then query the videos table again using the trailer_id value as the equality condition for the video_id column. What is the URL for the trailer?

```
SELECT * FROM trailers_by_video WHERE video_id = 8a657435-0ef2-11e5-91b1-8438355b7e3a;
SELECT * FROM videos WHERE video_id=8a65751c-0ef2-11e5-9cac-8438355b7e3a;

URL: https://www.youtube.com/watch?v=esGn-xKFZdU
```