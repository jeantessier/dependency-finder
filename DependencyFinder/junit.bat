@echo off

rem
rem  Copyright (c) 2001-2006, Jean Tessier
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

set LOCAL_CLASSPATH=%DEPENDENCYFINDER_HOME%\classes
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\src
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\tests
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\lib\log4j.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\lib\jakarta-oro.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JUNIT_HOME%\junit.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%FITLIBRARY_HOME%\fitlibraryRunner.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\lib\httpunit.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\js.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\jsunit.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\nekohtml.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\servlet.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\Tidy.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\xercesImpl.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\xmlParserAPIs.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\bin\commons-daemon.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\bin\commons-logging-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\commons-el.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\jasper-compiler-jdt.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\jasper-compiler.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\jasper-runtime.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\jsp-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\naming-factory-dbcp.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\naming-factory.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\naming-resources.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\common\lib\servlet-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\catalina-ant-jmx.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\catalina-ant.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\catalina-cluster.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\catalina-optional.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\catalina-storeconfig.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\catalina.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\commons-modeler.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\servlets-default.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\servlets-invoker.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\servlets-webdav.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\tomcat-ajp.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\tomcat-apr.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\tomcat-coyote.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\tomcat-http.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\tomcat-jkstatus-ant.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\server\lib\tomcat-util.jar

if "%DEPENDENCYFINDER_CONSOLE%"=="" goto noConsole
"%JAVA_HOME%\bin\java" %DEPENDENCYFINDER_OPTS% -DDEPENDENCYFINDER_TESTS_VALIDATE=%DF_VALIDATE% -classpath %LOCAL_CLASSPATH% junit.swingui.TestRunner %DEPENDENCYFINDER_CMD_LINE_ARGS%
goto doneRun
:noConsole
start "JUnit" "%JAVA_HOME%\bin\javaw" %DEPENDENCYFINDER_OPTS% -DDEPENDENCYFINDER_TESTS_VALIDATE=%DF_VALIDATE% -classpath %LOCAL_CLASSPATH% junit.swingui.TestRunner %DEPENDENCYFINDER_CMD_LINE_ARGS%
:doneRun

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
