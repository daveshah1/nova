import serial
import time
import subprocess
gpsPort = None

def begin():
    global gpsPort
    #Route UART2 to external I/O
    subprocess.call(["devmem2","0x48002170","w","0x011C011C"]) 
    subprocess.call(["devmem2","0x48002178","w","0x01180000"]) 
    #Open serial port
    gpsPort = serial.Serial("/dev/ttyS1",9600,timeout=3);
    while(True):
        s = gpsPort.readline()
        if(s[:6] == "$GPGGA"): #Only interested in position updates for now
            #Save to text file as a crude means of IPC
            with open('gps-position.txt', 'w') as f:
                f.write(s)
begin()
