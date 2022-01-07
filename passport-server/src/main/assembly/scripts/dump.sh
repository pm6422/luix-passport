#!/bin/bash

APP_NAME="${project.build.finalName}.${project.packaging}"
DUMP_DIR=`date +%Y%m%d%H%M%S`


PIDS=`ps -ef | grep java | grep "$APP_NAME" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $APP_NAME does not started!"
    exit 1
fi

if [ ! -d $DUMP_DIR ]; then
    mkdir $DUMP_DIR
fi


echo -e "Dumping the $APP_NAME ...\c"
for PID in $PIDS ; do
	jstack $PID > $DUMP_DIR/jstack-$PID.txt 2>&1
	echo -e ".\c"
	jinfo $PID > $DUMP_DIR/jinfo-$PID.txt 2>&1
	echo -e ".\c"
	jstat -gcutil $PID > $DUMP_DIR/jstat-gcutil-$PID.txt 2>&1
	echo -e ".\c"
	jstat -gccapacity $PID > $DUMP_DIR/jstat-gccapacity-$PID.txt 2>&1
	echo -e ".\c"
	jmap $PID > $DUMP_DIR/jmap-$PID.txt 2>&1
	echo -e ".\c"
	jmap -heap $PID > $DUMP_DIR/jmap-heap-$PID.txt 2>&1
	echo -e ".\c"
	jmap -histo $PID > $DUMP_DIR/jmap-histo-$PID.txt 2>&1
	echo -e ".\c"
	if [ -r /usr/sbin/lsof ]; then
	/usr/sbin/lsof -p $PID > $DUMP_DIR/lsof-$PID.txt
	echo -e ".\c"
	fi
done

if [ -r /bin/netstat ]; then
/bin/netstat -an > $DUMP_DIR/netstat.txt 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/iostat ]; then
/usr/bin/iostat > $DUMP_DIR/iostat.txt 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/mpstat ]; then
/usr/bin/mpstat > $DUMP_DIR/mpstat.txt 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/vmstat ]; then
/usr/bin/vmstat > $DUMP_DIR/vmstat.txt 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/free ]; then
/usr/bin/free -t > $DUMP_DIR/free.txt 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/sar ]; then
/usr/bin/sar > $DUMP_DIR/sar.txt 2>&1
echo -e ".\c"
fi
if [ -r /usr/bin/uptime ]; then
/usr/bin/uptime > $DUMP_DIR/uptime.txt 2>&1
echo -e ".\c"
fi

echo "OK!"
echo "Dump Directory: $DUMP_DIR"
