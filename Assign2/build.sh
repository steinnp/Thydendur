#!/bin/bash

javac ./compilerproj/*.java
jar cfm Parser.jar Manifest.txt ./compilerproj/*.class
