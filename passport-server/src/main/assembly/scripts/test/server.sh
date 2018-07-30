#!/bin/bash
cd `dirname $0`
if [ "$1" = "start" ]; then
	bash start.sh
else
	if [ "$1" = "stop" ]; then
		bash stop.sh
	else
		if [ "$1" = "debug" ]; then
			bash start.sh debug
		else
			if [ "$1" = "restart" ]; then
				bash restart.sh
			else
				if [ "$1" = "dump" ]; then
					bash dump.sh
				else
					echo "ERROR: Please input argument: start or stop or debug or restart or dump"
				    exit 1
				fi
			fi
		fi
	fi
fi
