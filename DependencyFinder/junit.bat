@echo off

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem %~dp0 is name of current script under NT
set DEFAULT_DEPENDENCYFINDER_HOME=%~dp0

rem : operator works similar to make : operator
set DEFAULT_DEPENDENCYFINDER_HOME=%DEFAULT_DEPENDENCYFINDER_HOME:\bin\=%

if %DEPENDENCYFINDER_HOME%a==a set DEPENDENCYFINDER_HOME=%DEFAULT_DEPENDENCYFINDER_HOME%
set DEFAULT_DEPENDENCYFINDER_HOME=

rem On NT/2K grab all arguments at once
set DEPENDENCYFINDER_CMD_LINE_ARGS=%*
goto doneStart

:win9xStart
rem Slurp the command line arguments.  This loop allows for an unlimited number of 
rem agruments (up to the command line limit, anyway).

set DEPENDENCYFINDER_CMD_LINE_ARGS=

:setupArgs
if %1a==a goto doneStart
set DEPENDENCYFINDER_CMD_LINE_ARGS=%DEPENDENCYFINDER_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart
rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.

start %JAVA_BIN%\javaw %DEPENDENCYFINDER_OPTS% -Dlog4j.configuration=file:///%DEPENDENCYFINDER_HOME%/etc/log4j.properties -classpath %DEPENDENCYFINDER_HOME%\classes;%DEPENDENCYFINDER_HOME%\lib\log4j-core.jar;%DEPENDENCYFINDER_HOME%\lib\jakarta-oro-2.0.4.jar;%DEPENDENCYFINDER_HOME%\lib\xerces.jar;c:\lib\junit.jar junit.swingui.TestRunner %DEPENDENCYFINDER_CMD_LINE_ARGS%

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
