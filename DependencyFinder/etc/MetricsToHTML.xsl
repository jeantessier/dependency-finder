<?xml version="1.0"?> 

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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:strip-space elements="*"/>

    <xsl:template match="metrics">
        <html>

        <head>
            <link rel="stylesheet" type="text/css" href="metrics.style.css" />
            <title>Metrics for <xsl:apply-templates select="project/name"/></title>
        </head>

        <body>
            <xsl:apply-templates/>
        </body>

        </html>
    </xsl:template>

    <xsl:template match="project">
        <div class="projectname"><a name="{name}"><xsl:value-of select="name"/></a></div>
        <div class="measurements"><xsl:apply-templates select="measurement"/></div>
        <div class="groups"><xsl:apply-templates select="group"/></div>
    </xsl:template>

    <xsl:template match="group">
        <div class="groupname"><a name="{name}"><xsl:value-of select="name"/></a></div>
        <div class="measurements"><xsl:apply-templates select="measurement"/></div>
        <div class="classes"><xsl:apply-templates select="class"/></div>
    </xsl:template>

    <xsl:template match="class">
        <div class="classname"><a name="{name}"><xsl:value-of select="name"/></a></div>
        <div class="measurements"><xsl:apply-templates select="measurement"/></div>
        <div class="methods"><xsl:apply-templates select="method"/></div>
    </xsl:template>

    <xsl:template match="method">
        <div class="methodname"><a name="{name}"><xsl:value-of select="name"/></a></div>
        <div class="measurements"><xsl:apply-templates select="measurement"/></div>
    </xsl:template>

    <xsl:template match="measurement">
        <span class="measurementname"><xsl:value-of select="long-name"/></span>: <span class="measurementvalue"><xsl:value-of select="value"/></span><br />
        <xsl:apply-templates select="members"/>
    </xsl:template>

    <xsl:template match="measurement[minimum]">
        <span class="measurementname"><xsl:value-of select="long-name"/></span>: <span class="measurementvalue"><xsl:value-of select="value"/> [<xsl:value-of select="minimum"/>, <xsl:value-of select="median"/>, <xsl:value-of select="average"/>, <xsl:value-of select="standard-deviation"/>, <xsl:value-of select="maximum"/>, <xsl:value-of select="sum"/>, <xsl:value-of select="nb-data-points"/>]</span><br />
    </xsl:template>

    <xsl:template match="members">
        <div class="members"><xsl:apply-templates/></div>
    </xsl:template>

    <xsl:template match="member">
        <span class="member"><a href="#{text()}"><xsl:value-of select="text()"/></a></span><br />
    </xsl:template>

</xsl:stylesheet>
