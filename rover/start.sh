#!/bin/bash
export newfile=`ls /home/root/log/ | sed 's/_/ _/' | sort -rn | awk '{printf "%03d", $1 + 1; exit}'`
export DIR=/home/root/log/$newfile
mkdir $DIR
echo $DIR
/home/root/rover/camtest.sh &
/home/root/rover/mainprog.sh &
