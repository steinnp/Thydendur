#!/bin/bash

javac ./compilerproj/*.java
jar cfm LexAnal.jar Manifest.txt ./compilerproj/*.class
