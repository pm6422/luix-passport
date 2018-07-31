#!/bin/bash
# enter current directory of the shell
cd `dirname $0`

#------------------------------------------------------------------------------------------------------------
# Set variables
#------------------------------------------------------------------------------------------------------------
appDir=../lib
appName="${project.build.finalName}.${project.packaging}"
serverPort=${server.port}
profiles="${mvn.profiles.active}"
appStartLog="../start.log"
appStartedIndicatorText="Application is running"

#------------------------------------------------------------------------------------------------------------
# Check the existing process
#------------------------------------------------------------------------------------------------------------
function checkProcess() {
    PIDS=`ps -ef | grep java | grep "$appName" | awk '{print $2}'`
    
    if [ -n "$PIDS" ]; then
        echo "Start failure: The $appName already started!"
        echo "PID: $PIDS"
        exit 1
    fi
}

#------------------------------------------------------------------------------------------------------------
# Delete start log file
#------------------------------------------------------------------------------------------------------------
function deleteStartLog() {
    if [ -f "$appStartLog" ]; then
        rm -rf "$appStartLog"
        echo "Deleted start log"
    fi
}

#------------------------------------------------------------------------------------------------------------
# Start the application
#------------------------------------------------------------------------------------------------------------
function startApp() {
    JAVA_JMX_OPTS=""
    JAVA_DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
    JAVA_MEM_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -Xss256K"
    echo -e "Starting the $appName"
    echo "Starting command: nohup java $JAVA_MEM_OPTS -jar $appDir/$appName --logback.loglevel=INFO --spring.profiles.active=$profiles --server.port=$serverPort >> $appStartLog 2>&1 &\n"
    . /etc/profile
    nohup java $JAVA_MEM_OPTS -jar $appDir/$appName --logback.loglevel=INFO --spring.profiles.active=$profiles --server.port=$serverPort >> $appStartLog 2>&1 &
}

#------------------------------------------------------------------------------------------------------------
# Display the start log on terminal
#------------------------------------------------------------------------------------------------------------
function displayStartLog(){
    tail -f $appStartLog |
        while IFS= read line
            do
                echo "$line"
                if [[ "$line" == *"$appStartedIndicatorText"* ]]; then
                    pkill tail
                fi
        done
}

#------------------------------------------------------------------------------------------------------------
# Execute functions
#------------------------------------------------------------------------------------------------------------
checkProcess
deleteStartLog
startApp
displayStartLog
