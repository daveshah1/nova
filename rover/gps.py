import serial
import subprocess
import time
import traceback
gpsPort = None
currentSentence = ""
debug = False
def begin():
    global gpsPort
    #Route UART2 to external I/O
    subprocess.call(["devmem2","0x48002170","w","0x011C011C"]) 
    subprocess.call(["devmem2","0x48002178","w","0x01180000"]) 
    #Open serial port
    gpsPort = serial.Serial("/dev/ttyS1",9600,timeout=3)
    
def getSentence(code, timeout):
    global gpsPort, debug, currentSentence
    startTime = time.time()
    while((time.time() - startTime) < timeout):
        try:
            s = gpsPort.readline()
            if(len(s) > 10): 
                if(s[:6] == code): #Only interested in position updates for now
                    if(debug):
                        print "Got " + code + ": " + currentSentence
                    with open('gps.log', 'a') as f:
                        f.write("Recieved Sentence: " + s + "\n")
                    return s
        except:
            try: 
                #Log exception for failure analysis
                with open('gps.log', 'a') as f:
                    f.write("+++++ EXCEPTION +++++\n")
                    f.write(traceback.format_exc() + "\n")
                if(debug):
                    print traceback.format_exc()
            except:
                pass #Just incase there is an exception in the exception handler...
            return ""
    return ""  

 
#begin()

def main():
    global debug
    debug = True
    begin()
    while(True):
        getSentence("$GPGGA",3)
if __name__ == "__main__":
    main()