#!/usr/bin/env python

from struct import unpack; from os import urandom
import csv
import sys

def randMatrix(r, c):
    mtr = [[unpack("!Q", urandom(8))[0] - sys.maxint for i in range(c)] for j in range(r)]
    total = sum([sum(row) for row in mtr])
    print "Average: ", total/len(mtr)/len(mtr[0])
    return mtr

if __name__ == '__main__':
    size = 19
    numPlayers = 4
    maxCaptures = 10

    BoardHashVals = randMatrix(size*size, numPlayers)
    CaptureHashVals = randMatrix(maxCaptures/2, numPlayers)

    with open("BoardHashVals.csv", "wb") as f:
        writer = csv.writer(f)
        writer.writerows(BoardHashVals)

    with open("CaptureHashVals.csv", "wb") as f:
        writer = csv.writer(f)
        writer.writerows(CaptureHashVals)