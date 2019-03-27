#!/bin/bash
cd `dirname $0`

#------------------------------------------------------------------------------------------------------------
# Set variables
#------------------------------------------------------------------------------------------------------------
appName="${project.build.finalName}.${project.packaging}"
serverPort=${app.server.port}

#------------------------------------------------------------------------------------------------------------
# Check the existing process
#------------------------------------------------------------------------------------------------------------
function checkProcess() {
    PIDS=`ps -ef | grep java | grep -v "grep" | grep "$serverPort" | awk '{print $2}'`

    if [ -z "$PIDS" ]; then
        echo "Stop warning: The $appName does not start!"
        exit 1
    fi
}

#------------------------------------------------------------------------------------------------------------
# Stop the application
#------------------------------------------------------------------------------------------------------------
function stopApp() {
    echo "Stopping the $appName"
    COUNT=0
    while [ $COUNT -lt 10 ]; do
        PID=`ps -ef | grep java |  grep -v "grep" | grep "$serverPort" | awk '{print $2}'`
        kill $PID > /dev/null 2>&1
        sync;sync;sync
        sleep 2
        sync;sync;sync
        if [[ -z "$PID" ]]; then
            echo "Stopped the $appName"
            exit 0
        fi
        echo "Trying to stop $((${COUNT}+1)) times..."
        let COUNT++
    done

    PID2=`ps -ef | grep java | grep -v "grep" | grep "$serverPort" | awk '{print $2}'`
    if [[ ! -z $PID2  ]];then
        kill -9 $PID2  2&> /dev/null
        sync;sync;sync;
        sleep 2
        sync;sync;sync;
    fi
    PID3=`ps -ef | grep java | grep -v "grep" | grep "$serverPort" | awk '{print $2}'`
    if [[ -z $PID3 ]];then
        echo "Stopped the $appName"
    else 
        echo "Failed to stopped the $appName"
    fi

}

#------------------------------------------------------------------------------------------------------------
# Execute functions
#------------------------------------------------------------------------------------------------------------
checkProcess
stopApp