#!/bin/bash

dmesg -n1 #Quiten down driver

#UART2

devmem2 0x48002170 w 0x011C011C
devmem2 0x48002178 w 0x01180000

#Web server
httpd -c /home/root/httpd.conf


#Weird initialisation sequence required for a slightly dodgy wireless driver
timeout 5 ifup wlan0
sleep 2
timeout 5 ifdown wlan0
sleep 2
timeout 60 ifup wlan0
while [ 1 ]
do	
	#Check if network is alive
        ping -w4 -c1 192.168.1.1
 	rc=$?
	if [[ $rc != 0 ]] ; then
                echo Network failed
		timeout 5 ifdown wlan0
		sleep 3
                timeout 60 ifup wlan0
	fi
	sleep 10
done
 
