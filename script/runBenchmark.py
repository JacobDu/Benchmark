# -*- coding: utf-8 -*-  

import os

MODES = ['Atomic','Adder','ReadWriteLock','ReentrantLock','StampedLock']
NUM = [1,2,4,8,16,32,64,128]

def execCmd(cmd):
    return os.popen(cmd)

def buildCmd(mode,readThread,writeThread):
    return 'java -jar ../target/Benchmark.jar %s %s %s true' %(mode,readThread,writeThread)    

if __name__ == '__main__':
    for mode in MODES:
        for numThread in NUM:
            print 'Mode=%s ; ReadThread=%s ; WriteThread=%s START!' %(mode,numThread,numThread)  
            print execCmd(buildCmd(MODES,numThread,numThread)).readlines()
            print 'Mode=%s ; ReadThread=%s ; WriteThread=%s FINISH!' %(mode,numThread,numThread)  

