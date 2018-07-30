#!/bin/bash
cd `dirname $0`

#------------------------------------------------------------------------------------------------------------
# Set variables
#------------------------------------------------------------------------------------------------------------
appName="${project.build.finalName}.${project.packaging}"

#------------------------------------------------------------------------------------------------------------
# Check the existing process
#------------------------------------------------------------------------------------------------------------
function checkProcess() {
    PIDS=`ps -ef | grep java | grep "$appName" | grep -v "grep" | awk '{print $2}'`
    
    if [ -z "$PIDS" ]; then
        echo "Stop warning: The $appName does not started!"
        exit 1
    fi
}

#------------------------------------------------------------------------------------------------------------
# Stop the application
#------------------------------------------------------------------------------------------------------------
function stopApp() {
    echo "Stopping the $appName ..."
    COUNT=0
    while [ $COUNT -lt 10 ]; do
        PID=`ps -ef | grep java | grep "$appName" | grep -v "grep" | awk '{print $2}'`
        kill $PID > /dev/null 2>&1
        sync
        sleep 2
        if [ -z "$PID" ]; then
            echo -e "\nStopped the $appName success"
            exit 0
        fi
        echo -n "."
        let COUNT++
    done

    PID2=`ps -ef | grep java | grep "$appName" | grep -v "grep" | awk '{print $2}'`
    if [[ ! -z $PID2  ]];then
        kill -9 $PID2  2&> /dev/null 
    fi
    PID3=`ps -ef | grep java | grep "$appName" | grep -v "grep" | awk '{print $2}'`
    if [[ -z $PID3  ]];then
        echo -e "\nStopped the $appName  Success"
    else 
        echo -e "\nStopped the $appName  Failed"
    fi

}

#------------------------------------------------------------------------------------------------------------
# Execute functions
#------------------------------------------------------------------------------------------------------------
checkProcess
stopApp

