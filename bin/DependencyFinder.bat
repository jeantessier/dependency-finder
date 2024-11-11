@echo off

rem
rem  Copyright (c) 2001-2024, Jean Tessier
rem  All rights reserved.
rem  
rem  Redistribution and use in source and binary forms, with or without
rem  modification, are permitted provided that the following conditions
rem  are met:
rem  
rem     * Redistributions of source code must retain the above copyright
rem       notice, this list of conditions and the following disclaimer.
rem  
rem     * Redistributions in binary form must reproduce the above copyright
rem       notice, this list of conditions and the following disclaimer in the
rem       documentation and/or other materials provided with the distribution.
rem  
rem     * Neither the name of Jean Tessier nor the names of his contributors
rem       may be used to endorse or promote products derived from this software
rem       without specific prior written permission.
rem  
rem  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
rem  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
rem  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
rem  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
rem  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
rem  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
rem  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
rem  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
rem  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
rem  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
rem  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
rem

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem %~dp0 is name of current script under NT
set DEFAULT_DEPENDENCYFINDER_HOME=%~dp0

rem : operator works similar to make : operator
set DEFAULT_DEPENDENCYFINDER_HOME=%DEFAULT_DEPENDENCYFINDER_HOME:\bin\=%

if "%DEPENDENCYFINDER_HOME%"=="" set DEPENDENCYFINDER_HOME=%DEFAULT_DEPENDENCYFINDER_HOME%
set DEFAULT_DEPENDENCYFINDER_HOME=

rem On NT/2K grab all arguments at once
set DEPENDENCYFINDER_CMD_LINE_ARGS=%*
goto doneStart

:win9xStart
rem Slurp the command line arguments.  This loop allows for an unlimited 
rem number of arguments (up to the command line limit, anyway).

set DEPENDENCYFINDER_CMD_LINE_ARGS=

:setupArgs
if %1a==a goto doneStart
set DEPENDENCYFINDER_CMD_LINE_ARGS=%DEPENDENCYFINDER_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart
rem This label provides a place for the argument list loop to break out
rem and for NT handling to skip to.

set DEPENDENCYFINDER_CLASSPATH=

SETLOCAL ENABLEDELAYEDEXPANSION
for %%j in (%DEPENDENCYFINDER_HOME%\lib\*.jar) do set DEPENDENCYFINDER_CLASSPATH=!DEPENDENCYFINDER_CLASSPATH!;%%j

if "%DEPENDENCYFINDER_CONSOLE%"=="" goto noConsole
"%JAVA_HOME%\bin\java" %DEPENDENCYFINDER_OPTS% -classpath "%DEPENDENCYFINDER_HOME%\classes;%DEPENDENCYFINDER_CLASSPATH%" com.jeantessier.dependencyfinder.gui.DependencyFinder %DEPENDENCYFINDER_CMD_LINE_ARGS%
goto doneRun
:noConsole
start "Dependency Finder" "%JAVA_HOME%\bin\javaw" %DEPENDENCYFINDER_OPTS% -classpath "%DEPENDENCYFINDER_HOME%\classes;%DEPENDENCYFINDER_CLASSPATH%" com.jeantessier.dependencyfinder.gui.DependencyFinder %DEPENDENCYFINDER_CMD_LINE_ARGS%
:doneRun

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
