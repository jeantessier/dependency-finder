@echo off

rem 
rem Dependency Finder - Computes quality factors from compiled Java code
rem Copyright (C) 2001  Jean Tessier
rem 
rem This library is free software; you can redistribute it and/or
rem modify it under the terms of the GNU Lesser General Public
rem License as published by the Free Software Foundation; either
rem version 2.1 of the License, or (at your option) any later version.
rem 
rem This library is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
rem Lesser General Public License for more details.
rem 
rem You should have received a copy of the GNU Lesser General Public
rem License along with this library; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
rem 

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

%JAVA_HOME%\bin\java %DEPENDENCYFINDER_OPTS% -Dlog4j.configuration=file:///%DEPENDENCYFINDER_HOME%/etc/log4j.properties -classpath %DEPENDENCYFINDER_HOME%\lib\DependencyFinder.jar;%DEPENDENCYFINDER_HOME%\lib\log4j-core.jar;%DEPENDENCYFINDER_HOME%\lib\jakarta-oro-2.0.4.jar;%DEPENDENCYFINDER_HOME%\lib\xerces.jar com.jeantessier.dependencyfinder.cli.DependencyClosure %DEPENDENCYFINDER_CMD_LINE_ARGS%

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
