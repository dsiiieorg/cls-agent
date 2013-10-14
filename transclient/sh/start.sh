#!/bin/sh

. /etc/profile
. ~/.bash_profile
PROG="dist/cls-agent"

PROC_STR=/usr/iie/transclient/sh/start.sh
CRONTAB_STR=`crontab -l`
PROC_RM40DAYS=rm40days

echo "CRONTAB_STR=$CRONTAB_STR"
echo "PROC_STR=$PROC_STR"

echo "$CRONTAB_STR" |grep -q "$PROC_STR"
if [ $? -eq 0 ]
then
    echo "cotain"
else
    echo "not contain"
    echo "*/1 * * * * /usr/iie/transclient/sh/start.sh" >> /var/spool/cron/root
    crontab /var/spool/cron/root
fi

echo "$CRONTAB_STR" |grep -q "$PROC_RM40DAYS"
if [ $? -eq 0 ]
then
    echo "rm40days cotain"
else
    echo "rm40days not contain"
    echo "0 0 * * * /usr/iie/transclient/sh/rm40daysStart.sh" >> /var/spool/cron/root
    crontab /var/spool/cron/root
fi

cd /usr/iie/transclient/cls-agent
PRONUM=`ps aux | grep "$PROG" |grep -v grep |wc -l`
if  test $PRONUM -lt 1
then
#        nohup java -jar /usr/iie/transclient/cls-agent.jar &
         nohup java -jar /usr/iie/transclient/cls-agent/dist/cls-agent.jar &
else
        echo "already exits!"
fi
