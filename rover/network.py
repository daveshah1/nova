import SocketServer
import arduino
from threading import Timer

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        print "{} wrote:".format(self.client_address[0])
        print self.data
        # just send back the same data, but upper-cased
        if(self.data[3:5] == "PI"):        
            self.request.sendall("RP OK\n")
        elif(self.data[3:5] == "CL"):
            self.request.sendall("RP OK 51.487556 -0.2381855")
        elif(self.data[3:5] == "MV"):
			if(len(self.data) < 7):
				self.request.sendall("RP NP\n")
			else:
				if(self.data[6] == "F"):
					status = arduino.motorCtl("F","F")
				elif(self.data[6] == "L"):
					status = arduino.motorCtl("B","F")
					t = Timer(1,lambda : arduino.motorCtl("S","S"))
					t.start()
				elif(self.data[6] == "R"):
					status = arduino.motorCtl("F","B")
					t = Timer(1,lambda : arduino.motorCtl("S","S"))
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
			temperature, pressure = arduino.readTP()
			if ((temperature == -1) and (pressure == -1)):
				self.request.sendall("RP OE\n")
			else:
				self.request.sendall("RP OK " + str(temperature) + " " + str(pressure) + "\n")
        else:
            self.request.sendall("RP NC")

def main():
    HOST, PORT = "localhost", 3141
    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    arduino.begin()
    server.serve_forever()
if __name__ == "__main__":
	main()