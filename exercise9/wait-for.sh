#!/usr/bin/env sh

if [ "$#" -ne 2 ]; then
    >&2 echo "Usage: $0 host:port sleep_time"
    exit -1
fi

ARGUMENT=$1
SLEEP_TIME=$2
HOST="$(echo $ARGUMENT | cut -d ':' -f1)"
PORT="$(echo $ARGUMENT | cut -d ':' -f2)"
MAX_RETRY=90

echo "Testing connection to host $HOST and port $PORT."

count=0
while [ $count -lt $MAX_RETRY ]
do
    count=$((count+1))
    nc -z $HOST $PORT
    result=$?
    if [ $result -eq 0 ]; then
        echo "Connection is available after $count second(s)."
        exit 0
    fi
    echo "Retrying..."
    sleep $SLEEP_TIME
done

>&2 echo "Timeout occurred after waiting $MAX_RETRY seconds for $HOST:$PORT."
exit -1