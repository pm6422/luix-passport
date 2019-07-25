#!/bin/bash
# enter current directory of the shell
cd `dirname $0`

#------------------------------------------------------------------------------------------------------------
# Set variables
#------------------------------------------------------------------------------------------------------------
appDir=../lib
appName="${project.build.finalName}.${project.packaging}"
serverPort=${app.server.port}
profiles="${spring.profiles.active}"
appStartLog="../start.log"
appStartedIndicatorText="Application is running"

#------------------------------------------------------------------------------------------------------------
# Check the existing process
#------------------------------------------------------------------------------------------------------------
function checkProcess() {
    echo "Starting the $appName"
    PIDS=`ps -ef | grep java | grep "$appName" | awk '{print $2}'`

    if [ -n "$PIDS" ]; then
        echo "Start failure: The $appName already started!"
        echo "PID: $PIDS"
        exit 1
    fi

    COUNT=0
    while [ $COUNT -lt 20 ]; do
        SERVER_PORT_COUNT=`netstat -tln | grep -w $serverPort | wc -l`
        sync;sync;sync
        sleep 2
        sync;sync;sync
        if [[ $SERVER_PORT_COUNT -lt 1 ]]; then
            break
        fi
        echo "Waiting to stop $((${COUNT}+1)) times..."
        let COUNT++
    done
}

#------------------------------------------------------------------------------------------------------------
# Clear or create start log file
#------------------------------------------------------------------------------------------------------------
function clearStartLog() {
    echo "" > $appStartLog
}

#------------------------------------------------------------------------------------------------------------
# Run the application
#------------------------------------------------------------------------------------------------------------
function runApp() {
    JAVA_CMD="nohup java -jar $appDir/$appName --logging.level.root=INFO --spring.profiles.active=$profiles --server.port=$serverPort >> $appStartLog 2>&1 &"
    echo "Command: $JAVA_CMD"
    nohup java $JAVA_MEM_OPTS -jar $appDir/$appName --logging.level.root=INFO --spring.profiles.active=$profiles --server.port=$serverPort >> $appStartLog 2>&1 &
}

#------------------------------------------------------------------------------------------------------------
# Display the start log on terminal
#------------------------------------------------------------------------------------------------------------
function displayStartLog(){
    echo "************************************************************************************************************************************"
    tail -f $appStartLog |
        while IFS= read line
            do
                echo "$line"
                if [[ "$line" == *"$appStartedIndicatorText"* ]]; then
                    pkill tail
                fi
        done
    echo "Started the $appName"
}

#------------------------------------------------------------------------------------------------------------
# Execute functions
#------------------------------------------------------------------------------------------------------------
checkProcess
clearStartLog
runApp
displayStartLog