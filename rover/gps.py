import serial
import time
import subprocess
from pynmea import nmea
gpsPort = None

def begin():
    global gpsPort
    #Enable UART2
    subprocess.call(["devmem2","0x48002170","w","0x011C011C"]) 
    subprocess.call(["devmem2","0x48002178","w","0x01180000"]) 
    gpsPort = serial.Serial("/dev/ttyO1",9600,timeout=3);
    while(True):
        s = gpsPort.readline()
        if(s[:6] == "$GPGGA"):
            print s
begin()
