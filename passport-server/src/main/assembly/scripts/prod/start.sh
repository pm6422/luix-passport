#!/bin/bash
# enter current directory of the shell
cd `dirname $0`

appDir=../lib
appName="${project.build.finalName}.${project.packaging}"
serverPort=${server.port}
profiles="${mvn.profiles.active}"
appStartLog="./start.log"
appStartedIndicatorText="Application is running"

PIDS=`ps -ef | grep java | grep "$appName" | awk '{print $2}'`

if [ -n "$PIDS" ]; then
    echo "ERROR: The $appName already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$serverPort" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $serverPort | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $appName port $serverPort already used!"
        exit 1
    fi
fi


### Function Definitions ###
########################################################
function run() {
    JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
    JAVA_MEM_OPTS=""
    BITS=`java -version 2>&1 | grep -i 64-bit`
    if [ -n "$BITS" ]; then
        JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
    else
        JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
    fi
    echo -e "Starting the $appName"
    echo "Start Command: nohup java -jar $appDir/$appName --logback.loglevel=INFO --spring.profiles.active=$profiles --server.port=$serverPort >> $appStartLog 2>&1 &\n"
    . /etc/profile
    nohup java -jar $appDir/$appName --logback.loglevel=INFO --spring.profiles.active=$profiles --server.port=$serverPort >> $appStartLog 2>&1 &
}

function watchStartLog(){
    tail -f $appStartLog |
        while IFS= read line
            do
                echo "$line"
                if [[ "$line" == *"$appStartedIndicatorText"* ]]; then
                    pkill tail
                fi
        done
}
### Functions Call ###
########################################################
run
watchStartLog
