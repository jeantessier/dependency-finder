@echo off

rem
rem  Copyright (c) 2001-2002, Jean Tessier
rem  All rights reserved.
rem  
rem  Redistribution and use in source and binary forms, with or without
rem  modification, are permitted provided that the following conditions
rem  are met:
rem  
rem  	* Redistributions of source code must retain the above copyright
rem  	  notice, this list of conditions and the following disclaimer.
rem  
rem  	* Redistributions in binary form must reproduce the above copyright
rem  	  notice, this list of conditions and the following disclaimer in the
rem  	  documentation and/or other materials provided with the distribution.
rem  
rem  	* Neither the name of the Jean Tessier nor the names of his contributors
rem  	  may be used to endorse or promote products derived from this software
rem  	  without specific prior written permission.
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

java -Dlog4j.configuration=file:///%DEPENDENCYFINDER_HOME%/etc/log4j.properties -classpath classes;lib\log4j-core.jar;lib\jakarta-oro-2.0.4.jar;lib\xerces.jar;c:\lib\junit.jar junit.textui.TestRunner %1 %2 %3 %4 %5 %6 %7 %8 %9
