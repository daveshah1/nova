import SocketServer
import arduino
import gps
import datahandler
import navigator
import time
from threading import Timer
lastCommTime = time.time()
deployed = False
class MyTCPHandler(SocketServer.BaseRequestHandler):
    
    def handle(self):
        global lastCommTime, deployed
        lastCommTime = time.time()
        self.data = self.request.recv(1024).strip()
        print "{0} wrote:".format(self.client_address[0])
        print self.data
        if(self.data[3:5] == "PI"):     
            self.request.sendall("RP OK\n")
        elif(self.data[3:5] == "CL"):
            self.request.sendall("RP OK " + str(datahandler.latestLat) + " " + str(datahandler.latestLon) + "\n")
        elif(self.data[3:5] == "MV"):
            if(len(self.data) < 7):
                self.request.sendall("RP NP\n")
            else:
                if(self.data[6] == "F"):
                    deployed = True
                    navigator.manualForward()
                    status = True
                elif(self.data[6] == "L"):
                    t = Timer(0,lambda : navigator.manualTurn(0,90)) #Run in background
                    t.start()
                    status = True  
                elif(self.data[6] == "R"):
                    t = Timer(0,lambda : navigator.manualTurn(1,90)) #Run in background
                    t.start()  
                    status = True
                elif(self.data[6] == "B"):
                    navigator.manualBackward()
                    status = True
                else:
                    status = False
                if(status):
                    self.request.sendall("RP OK\n")
                else:
                    self.request.sendall("RP OE\n")
        elif(self.data[3:5] == "ST"):        
            navigator.manualStop()
            self.request.sendall("RP OK\n")
        elif(self.data[3:5] == "GT"):
            splitd = self.data[6:].split(' ')
            navigator.navigateAutomatically(float(splitd[0]),float(splitd[1]))
            self.request.sendall("RP OK\n")
        elif(self.data[3:5] == "FT"):
            arduino.formatEEPROM();
            self.request.sendall("RP OK\n")
        elif(self.data[3:5] == "TP"):
            #temperature, pressure = arduino.readTP()
            if ((datahandler.latestT == -1) and (datahandler.latestP == -1)):
                self.request.sendall("RP OE\n")
            else:
                self.request.sendall("RP OK " + str(datahandler.latestT) + " " + str(datahandler.latestP) + "\n")
        elif(self.data[3:5] == "DP"):
            realDeployed = arduino.isDeployed()
            if(realDeployed):
                self.request.sendall("RP OK D\n")
            else:
                self.request.sendall("RP OK N\n")
        else:
            self.request.sendall("RP NC")

def depCheck():
    global deployed, lastCommTime
    if((time.time() - lastCommTime) > 90):
        if(arduino.isDeployed()):        
            deployed = True
            navigator.navigateAutomatically(0,0) #Update
        else:
            deployed = False
        lastCommTime = time.time()
    else:
        if(arduino.isDeployed() == False):        
            lastCommTime = time.time()

        

def main():
    HOST, PORT = "", 9001 #Bind to 192.168.1.39, port 9001 
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
    gps.begin()
    datahandler.begin()
    #gpsTimer = Timer(1,gps.backgroundCheck) #Start GPS Background updater
    #gpsTimer.start()
    
    arduino.begin() #Open communications with Arduino
    backgroundTimer = Timer(1,datahandler.backgroundUpdater)
    backgroundTimer.start()
    
    navTimer = Timer(1.5,navigator.update)
    navTimer.start()
    
    depCheckTimer = Timer(5,depCheck)
    depCheckTimer.start()
    
    print 'Starting server'
    server.serve_forever() #Start listening for requests.
if __name__ == "__main__":
    main()
