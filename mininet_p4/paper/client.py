#!/usr/bin/env python
"""
Multi-threaded TCP Client
multithreadedClient.py is a TCP client that maintains a maximum number of worker threads which continuously send a given
number of requests to multithreadedServer.py and print the server's response.
This is derived from an assignment for the Distributed Systems class at Bennington College
"""

from argparse import ArgumentParser
from socket import SO_REUSEADDR, SOCK_STREAM, error, socket, SOL_SOCKET, AF_INET
import time
import json

# -------------------------------------#
########## ARGUMENT HANDLING ##########
#-------------------------------------#

# Initialize instance of an argument parser
parser = ArgumentParser(description='TCP Client')

# Add optional arguments, with given default values if user gives no args

parser.add_argument('-i', '--ip', default='127.0.0.1', help='IP address to connect over')
parser.add_argument('-p', '--port', default=9000, type=int, help='Port over which to connect')
parser.add_argument('-a', '--action', default='visit', help='generate or visit')
parser.add_argument('-c', '--coordinate', required=True,  help='Coordinates of the data to be generated or visited')
parser.add_argument('-host', '--host_id', default = -1, type = int, help='When generate, the host where store the data')

# Get the arguments
args = parser.parse_args()

#--------------------------------------#
########## CLIENT CONSTRUCTOR ##########
#--------------------------------------#


class Client:
    def __init__(self, id, address, port):
        self.s = socket(AF_INET, SOCK_STREAM)
        self.id = id
        self.address = address
        self.port = port

    def run(self):
        try:
            # Timeout if the no connection can be made in 5 seconds
            self.s.settimeout(5)
            # Allow socket address reuse
            self.s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
            # Connect to the ip over the given port
            self.s.connect((self.address, self.port))
            # Send the defined request message
            d = dict(action=args.action, coordinate=args.coordinate, host_id=args.host_id)
            message = json.dumps(d)
            start = round(time.time()*1000)
            self.s.send(message)


            # Wait to receive data back from server
            data = self.s.recv(1024)
            end = round(time.time()*1000)
            timeused = end-start
            print(timeused)
            # Notify that data has been received
            if(data == "-1"):
                d = dict(action="ack", coordinate=args.coordinate, res="fail", timeused=timeused)
                message = json.dumps(d)
                self.s.send(message)
            else:
                d = dict(action="ack", coordinate=args.coordinate, res="success", timeused=timeused)
                message = json.dumps(d)
                self.s.send(message)
            # CLOSE THE SOCKET
            self.s.close()
        # If something went wrong, notify the user
        except error as e:
            print "\nERROR: Could not connect to ", self.address, " over port", self.port, "\n"
            raise e



# Function which generates a Client instance, getting the work item to be processed from the queue
def worker():
    new_client = Client(0, args.ip, args.port)
    new_client.run()


#--------------------------------------------------#
########## INITIATE CLIENT WORKER THREADS ##########
#--------------------------------------------------#

worker()