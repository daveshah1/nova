#!/bin/bash
dmesg -n1 #Quiten down driver

#Weird initialisation sequence required for a slightly dodgy wireless driver
timeout 5 ifup wlan0
timeout 5 ifdown wlan0
timeout 30 ifup wlan0
while [ 1 ]
do	
	#Check if network is alive
	if ! [ "$(ping -q -c1 192.168.1.1)" ]; then
		timeout 5 ifdown wlan0
		timeout 30 ifup wlan0
	fi
	sleep 10
done
 