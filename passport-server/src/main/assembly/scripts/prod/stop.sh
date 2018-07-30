#!/bin/bash
cd `dirname $0`

APP_DIR=../lib
APP_NAME="${project.build.finalName}.${project.packaging}"
SERVER_PORT=${server.port}

PIDS=`ps -ef | grep java | grep "$APP_NAME" | awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $APP_NAME does not started!"
    exit 1
fi

echo -e "Stopping the $APP_NAME ...\n"
for PID in $PIDS;do
    kill $PID > /dev/null 2>&1
done

COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS;do
        PID_EXIST=`ps -f -p $PID | grep java`
	if [ -n "$PID_EXIST" ]; then
	    COUNT=0
            break
	fi
    done
done

echo "PID: $PIDS"
echo "$APP_NAME is stopped."