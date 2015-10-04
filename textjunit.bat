@echo off

rem
rem  Copyright (c) 2001-2009, Jean Tessier
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
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\lib\guava.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\lib\jakarta-oro.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\lib\jsr305.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%DEPENDENCYFINDER_HOME%\lib\log4j.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JUNIT_HOME%\junit-4.4.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\cglib-2.1_3-src.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\cglib-nodep-2.1_3.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\hamcrest-core-1.1.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\hamcrest-library-1.1.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\jmock-2.4.0.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\jmock-junit3-2.4.0.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\jmock-junit4-2.4.0.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\jmock-legacy-2.4.0.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%JMOCK_HOME%\objenesis-1.0.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%FITLIBRARY_HOME%\fitlibraryRunner.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\lib\httpunit.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\activation-1.1.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\js-1.6R5.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\jtidy-4aug2000r7-dev.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\junit-3.8.1.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\mail-1.4.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\nekohtml-0.9.5.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\servlet-api-2.4.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\xercesImpl-2.6.1.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%HTTPUNIT_HOME%\jars\xmlParserAPIs-2.6.1.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\bin\bootstrap.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\bin\commons-daemon.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\bin\tomcat-juli.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\annotations-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\catalina-ant.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\catalina-ha.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\catalina-tribes.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\catalina.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\el-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\jasper-el.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\jasper-jdt.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\jasper.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\jsp-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\servlet-api.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\tomcat-coyote.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\tomcat-dbcp.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\tomcat-i18n-es.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\tomcat-i18n-fr.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CATALINA_HOME%\lib\tomcat-i18n-jp.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\commons-logging.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\freemarker.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\javamail.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\ognl.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\oscore.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\rife-continuations.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\lib\default\xwork.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%WEBWORK_HOME%\webwork-2.2.6.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%ANT_HOME%\lib\ant-junit.jar
set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%ANT_HOME%\lib\ant.jar

"%JAVA_HOME%\bin\java" %DEPENDENCYFINDER_OPTS% -DDEPENDENCYFINDER_TESTS_VALIDATE=%DF_VALIDATE% -classpath %LOCAL_CLASSPATH% org.junit.runner.JUnitCore %DEPENDENCYFINDER_CMD_LINE_ARGS%

if not "%OS%"=="Windows_NT" goto mainEnd
:winNTend
@endlocal

:mainEnd
