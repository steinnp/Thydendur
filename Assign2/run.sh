#!/bin/bash
java -jar Parser.jar error2.decaf 2> test.txt
wc -l test.txt
