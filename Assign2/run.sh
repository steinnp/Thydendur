#!/bin/bash
java -jar LexAnal.jar error2.decaf 2> test.txt
wc -l test.txt
