#!/bin/bash
. /etc/profile
. /root/.bash_profile

cd /usr/iie/transclient/rm40days
nohup java -jar /usr/iie/transclient/rm40days/dist/rm40days.jar 30 /usr/iie/transclient/download/ /usr/iie/transclient/log/cls_agent/ &
