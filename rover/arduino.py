#This file contains code related to I/O with the arduino

import serial
import time
arduinoPort = None

STATUS_OK = 1
STATUS_OFFLINE = 2
STATUS_CMD_ERROR = 3
STATUS_PARAM_ERROR = 4
STATUS_FAULT = 5
STATUS_UNKNOWN = 6
STATUS_COMMS_ERROR = 7
class CommandResponse:
    def __init__(self,status,data):
        self.status = status
        self.data = data

def sendRequest(command,data):
    try:
        arduinoPort.write("HI " + command + " " + data + "\n")
        response = arduinoPort.readline()
        if(len(response) < 5):
            return CommandResponse(STATUS_COMMS_ERROR)
        if(len(response) < 7):
            data = ""
        else:
            data = response[6:]
            
        if(response[3:5] == "OK"):
            return CommandResponse(STATUS_OK,data)
        elif(response[3:5] == "CE"):
            return CommandResponse(STATUS_COMMS_ERROR,data) 
        elif(response[3:5] == "NP"):
            return CommandResponse(STATUS_PARAM_ERROR,data)
        elif(response[3:5] == "NC"):
            return CommandResponse(STATUS_CMD_ERROR,data)  
        elif(response[3:5] == "OE"):
            return CommandResponse(STATUS_FAULT,data)
        else:
            return CommandResponse(STATUS_COMMS_ERROR,data)
    except:
        return CommandResponse(STATUS_COMMS_ERROR,"")
        
def testComms():
    response = sendRequest("PI","")
    if(response.status == STATUS_OK):
        return True
    else:
        return False
        
def stop():
    try:
        sendRequest("MT","S S")
    except:
        pass
    try:
        arduinoPort.close()
    except:
        pass

def begin():
    global arduinoPort
    arduinoPort = serial.Serial("/dev/ttyO0",57600,timeout=3)
    time.sleep(0.5)
    if(testComms()):
        return True
    else:
        stop()
        return False
    
def motorCtl(left,right):
    #Try up to 3 times
    for i in range(1,3):
        response = sendRequest("MT",left + " " + right)
        if(response.status == STATUS_OK):
            break

def readTP():
    for i in range(1,3):
        response = sendRequest("TP","")
        if(response.status == STATUS_OK):
            splitstr = response.data.split(" ")
            if(len(splitstr) < 2):
                continue
            return ( float(splitstr[0]) / 10, float(splitstr[1]) )
    return (-1,-1)