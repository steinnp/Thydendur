cd ParserRefImpl4
rm *.class
jflex-1.6.0/bin/jflex lexical.flex
javac *.java
jar cfm Project3.jar manifest.txt *.class
mv Project3.jar ..
