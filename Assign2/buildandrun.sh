#!/bin/bash
java -jar ./compilerproj/jflex-1.6.1/lib/jflex-1.6.1.jar ./compilerproj/lexical.flex
javac ./compilerproj/*.java
java compilerproj.TokenDumper input.txt
