#!/usr/bin/env python
"""
Multi-threaded TCP Server
multithreadedServer.py acts as a standard TCP server, with multi-threaded integration, spawning a new thread for each
client request it receives.
This is derived from an assignment for the Distributed Systems class at Bennington College
"""

from argparse import ArgumentParser
from threading import Lock, Thread
from socket import SO_REUSEADDR, SOCK_STREAM, socket, SOL_SOCKET, AF_INET
import json
import time

#---------------------------------------#
########## USER INPUT HANDLING ##########
#---------------------------------------#

# Initialize instance of an argument parser
parser = ArgumentParser(description='Multi-threaded TCP Server')

# Add optional argument, with given default values if user gives no arg
parser.add_argument('-i', '--IP', default='127.0.0.1', help='IP over which to connect')
parser.add_argument('-p', '--port', default=9000, type=int, help='Port over which to connect')

# Get the arguments
args = parser.parse_args()

# -------------------------------------------#
########## DEFINE GLOBAL VARIABLES ##########
#-------------------------------------------#

counter = 0
dataIndex = dict()
timeUsed = 0
success = 0
fail = 0
generate = 0
visit = 0
response_message = "Now serving, number: "
thread_lock = Lock()

# Create a server TCP socket and allow address re-use
s = socket(AF_INET, SOCK_STREAM)
s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
s.bind((args.IP, args.port))

# Create a list in which threads will be stored in order to be joined later
threads = []

#---------------------------------------------------------#
########## THREADED CLIENT HANDLER CONSTRUCTION ###########
#---------------------------------------------------------#


class ClientHandler(Thread):
    def __init__(self, address, port, socket, response_message, lock):
        Thread.__init__(self)
        self.address = address
        self.port = port
        self.socket = socket
        self.response_message = response_message
        self.lock = lock

    # Define the actions the thread will execute when called.
    def run(self):
        global counter
        global dataIndex
        global timeUsed
        global success
        global fail
        global visit
        global generate
        data = self.socket.recv(1024)
        # Lock the changing of the shared counter value to prevent erratic multithread changing behavior
        with self.lock:
            message = json.loads(data)
            if message["action"] == "generate" :
                generate+=1
                if message["coordinate"] in dataIndex:
                    self.socket.send("-1")
                else:
                    dataIndex[message["coordinate"]] = message["host_id"]
                    self.socket.send("0")
            elif message["action"] == "visit" :
                visit+=1
                if message["coordinate"] in dataIndex:
                    self.socket.send(str(dataIndex[message["coordinate"]]))
                else:
                    self.socket.send("-1")
            else:
                self.socket.send("-1")   

            data = self.socket.recv(1024)
            message = json.loads(data)
            if message["action"] == "ack" :
                counter+=1
                timeUsed += message["timeused"]
                if message["res"] == "success":
                    success+=1
                if message["res"] == "fail":
                    fail +=1
            print("timeused:%d,success:%d,fail:%d,generate:%d,visit:%d\r"%(message["timeused"],success,fail,generate,visit)) 
            print("====== total ====== time:%d, counter:%d"%(timeUsed,counter))
    
        self.socket.close()

#-----------------------------------------------#
########## MAIN-THREAD SERVER INSTANCE ##########
#-----------------------------------------------#

# Continuously listen for a client request and spawn a new thread to handle every request
while 1:

    try:
        # Listen for a request
        s.listen(1)
        # Accept the request
        sock, addr = s.accept()
        # Spawn a new thread for the given request
        newThread = ClientHandler(addr[0], addr[1], sock, response_message, thread_lock)
        newThread.start()
        threads.append(newThread)
    except KeyboardInterrupt:
        print "\nExiting Server\n"
        print (dataIndex)
        print ("timeUsed:%d"%timeUsed)
        print ("count:%d"%counter)
        break

# When server ends gracefully (through user keyboard interrupt), wait until remaining threads finish
for item in threads:
    item.join()