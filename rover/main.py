import SocketServer
import arduino
import gps
import datahandler
from threading import Timer

class MyTCPHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        self.data = self.request.recv(1024).strip()
        print "{0} wrote:".format(self.client_address[0])
        print self.data
        if(self.data[3:5] == "PI"):        
            self.request.sendall("RP OK\n")
        elif(self.data[3:5] == "CL"):
            self.request.sendall("RP OK " + + str(datahandler.latestLat) + " " + str(datahandler.latestLong) + "\n")
        elif(self.data[3:5] == "MV"):
            if(len(self.data) < 7):
                self.request.sendall("RP NP\n")
            else:
                if(self.data[6] == "F"):
                    status = arduino.motorCtl("F","F")
                elif(self.data[6] == "L"):
                    status = arduino.motorCtl("B","F") 
                    t = Timer(1,lambda : arduino.motorCtl("S","S")) #After turning for 1s, stop
                    t.start()
                elif(self.data[6] == "R"):
                    status = arduino.motorCtl("F","B")
                    t = Timer(1,lambda : arduino.motorCtl("S","S")) #After turning for 1s, stop
                    t.start()        
                elif(self.data[6] == "B"):
                    status = arduino.motorCtl("B","B")
                else:
                    status = False
                if(status):
                    self.request.sendall("RP OK\n")
                else:
                    self.request.sendall("RP OE\n")
        elif(self.data[3:5] == "ST"):        
            if(arduino.motorCtl("S","S")):
                self.request.sendall("RP OK\n")
            else:
                self.request.sendall("RP OE\n")
        elif(self.data[3:5] == "TP"):
            #temperature, pressure = arduino.readTP()
            if ((datahandler.latestT == -1) and (datahandler.latestP == -1)):
                self.request.sendall("RP OE\n")
            else:
                self.request.sendall("RP OK " + str(datahandler.latestT) + " " + str(datahandler.latestP) + "\n")
        else:
            self.request.sendall("RP NC")

def main():
    HOST, PORT = "0.0.0.0", 9001 #Bind to 0.0.0.0, port 9001 
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
    gps.begin()
    datahandler.begin()
    #gpsTimer = Timer(1,gps.backgroundCheck) #Start GPS Background updater
    #gpsTimer.start()
    
    arduino.begin() #Open communications with Arduino
    backgroundTimer = Timer(1,datahandler.backgroundUpdater())
    server.serve_forever() #Start listening for requests.
if __name__ == "__main__":
    main()
