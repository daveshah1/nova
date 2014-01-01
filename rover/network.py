import SocketServer

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
        else:
            self.request.sendall("RP NC")

def main():
    HOST, PORT = "localhost", 3141

    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
if __name__ == "__main__":
	main()