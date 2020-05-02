FROM datastax/dse-server:6.7.0 

USER root

RUN apt-get update && \
    apt-get install -y netcat

COPY wait-for.sh /opt/wait-for.sh

USER dse