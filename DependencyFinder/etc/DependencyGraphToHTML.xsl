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

    <xsl:template match="dependencies">
        <html>

        <head>
            <link rel="stylesheet" type="text/css" href="dependency.style.css" />
        </head>

        <body>
            <xsl:apply-templates/>
        </body>

        </html>
    </xsl:template>
  
    <xsl:template match="package">
        <xsl:choose>
            <xsl:when test="@confirmed='no'">
                <div class="packagename_inferred"><a name="{name}"></a><xsl:value-of select="name"/></div>
            </xsl:when>
            <xsl:otherwise>
                <div class="packagename"><a name="{name}"></a><xsl:value-of select="name"/></div>
            </xsl:otherwise>
        </xsl:choose>
        <div class="dependencies"><xsl:apply-templates select="outbound | inbound"/></div>
        <div class="classes"><xsl:apply-templates select="class"/></div>
    </xsl:template>
  
    <xsl:template match="class">
        <xsl:choose>
            <xsl:when test="@confirmed='no'">
                <div class="classname inferred"><a name="{name}"></a><xsl:value-of select="name"/></div>
            </xsl:when>
            <xsl:otherwise>
                <div class="classname"><a name="{name}"></a><xsl:value-of select="name"/></div>
            </xsl:otherwise>
        </xsl:choose>
        <div class="dependencies"><xsl:apply-templates select="outbound | inbound"/></div>
        <div class="features"><xsl:apply-templates select="feature"/></div>
    </xsl:template>
  
    <xsl:template match="feature">
        <xsl:choose>
            <xsl:when test="@confirmed='no'">
                <div class="featurename inferred"><a name="{name}"></a><xsl:value-of select="name"/></div>
            </xsl:when>
            <xsl:otherwise>
                <div class="featurename"><a name="{name}"></a><xsl:value-of select="name"/></div>
            </xsl:otherwise>
        </xsl:choose>
        <div class="dependencies"><xsl:apply-templates select="outbound | inbound"/></div>
    </xsl:template>
  
    <xsl:template match="inbound">
        <xsl:choose>
            <xsl:when test="@confirmed='no'">
                <span class="dependency inferred">&lt;-- <a class="inferred" href="#{.}"><xsl:value-of select="."/></a></span><br/>
            </xsl:when>
            <xsl:otherwise>
                <span class="dependency">&lt;-- <a href="#{.}"><xsl:value-of select="."/></a></span><br/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="outbound">
        <xsl:choose>
            <xsl:when test="@confirmed='no'">
                <span class="dependency inferred">--&gt; <a class="inferred" href="#{.}"><xsl:value-of select="."/></a></span><br/>
            </xsl:when>
            <xsl:otherwise>
                <span class="dependency">--&gt; <a href="#{.}"><xsl:value-of select="."/></a></span><br/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
</xsl:stylesheet>
