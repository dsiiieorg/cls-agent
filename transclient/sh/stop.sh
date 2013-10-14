#!/bin/sh

. /etc/profile


PROG="dist/cls-agent"
PRONUM=`ps aux | grep "$PROG" |grep -v grep |wc -l`
if  test $PRONUM -lt 1
then
        echo "no cls-agent!"
	# nohup java -jar /usr/iie/transclient/cls-agent.jar &
else
	PIDS=`ps aux | grep "$PROG" | grep -v grep | awk '{print $2}'`
	echo $PIDS
	`kill -9 $PIDS` 
        echo "has stop!"
fi
