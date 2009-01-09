<!--
    Copyright (c) 2001-2009, Jean Tessier
    All rights reserved.
    
    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:
    
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
    
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
    
        * Neither the name of Jean Tessier nor the names of his contributors
          may be used to endorse or promote products derived from this software
          without specific prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
    CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<jsp:useBean id="version" class="com.jeantessier.dependencyfinder.Version" scope="application"/>

<table class="footer">
    <tr>
        <td>
            <hr />
        </td>
    </tr>
    <tr>
        <td>
<%
            if (application.getAttribute("factory") != null) {
                if (application.getAttribute("updateStart") != null) {
%>
            Current graph last updated on <%= application.getAttribute("updateStart") %>.
<%
                } else if (application.getAttribute("extractStart") != null) {
%>
            Current graph extracted on <%= application.getAttribute("extractStart") %>.
<%
                } else if (application.getAttribute("loadStart") != null) {
%>
            Current graph loaded on <%= application.getAttribute("loadStart") %>.
<%
                }
            } else {
%>
            There is no dependency graph at this time.
<%
            }
%>
        </td>
    </tr>
    <tr>
        <td>
            Powered by
            <a href="<jsp:getProperty name="version" property="ImplementationURL"/>"><jsp:getProperty name="version" property="ImplementationTitle"/></a>
            <jsp:getProperty name="version" property="ImplementationVersion"/> (&copy; <jsp:getProperty name="version" property="CopyrightDate"/> <jsp:getProperty name="version" property="CopyrightHolder"/>)<br />
            Compiled on <jsp:getProperty name="version" property="ImplementationDate"/>.
        </td>
    </tr>
</table>
