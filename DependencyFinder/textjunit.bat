@echo off

java -Dlog4j.configuration=file:///%DEPENDENCYFINDER_HOME%/etc/log4j.properties -classpath classes;lib\log4j-core.jar;lib\jakarta-oro-2.0.4.jar;lib\xerces.jar;c:\lib\junit.jar junit.textui.TestRunner %1 %2 %3 %4 %5 %6 %7 %8 %9
