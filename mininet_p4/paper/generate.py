#!/usr/bin/env python

import pandas
from argparse import ArgumentParser
import subprocess
import os


def runCmd(cmd) :
        res = subprocess.Popen(cmd, shell=True,  stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
        sout ,serr = res.communicate()
        return res.returncode, sout, serr, res.pid 

parser = ArgumentParser(description='Data index generator')
parser.add_argument('-host', '--host_id', default = -1,  help='generate in which host')
args = parser.parse_args()

generate_data_path = "/home/p4/tutorials/exercises/paper/dataset/generate_dataset.csv"
generate_dataset = pandas.read_csv(generate_data_path)
for index, row in generate_dataset.iterrows():
    src = str(int(row["host_id"]))
    if src == args.host_id:
        target = str(int(row["target_host"]))
        target_ip = "10.0."+target+"."+target
        coordinate = "(%.4f,%.4f)"%(row["x"],row["y"])
        cmd = "./client.py -i %s -a \"generate\" -c \"%s\" -host %s"% (target_ip, coordinate, src)
        print(cmd)
        res = runCmd(cmd)




