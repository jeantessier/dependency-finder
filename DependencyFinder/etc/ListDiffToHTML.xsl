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

    <xsl:template match="list-diff">
        <html>

        <head>
            <title><xsl:if test="name/text()"><xsl:value-of select="name"/> - </xsl:if>Documentation Changes</title>
        </head>

        <body bgcolor="#ffffff">

        <h1><xsl:if test="name/text()"><xsl:value-of select="name"/> - </xsl:if>Documentation Changes</h1>

        <h2><xsl:value-of select="old"/> to <xsl:value-of select="new"/></h2>

        <xsl:apply-templates select="removed"/>

        <xsl:apply-templates select="added"/>

        </body>

        </html>
    </xsl:template>

    <xsl:template match="removed">
        <h3>No Longer in Published API:</h3>

        <font color="gray" size="-1">
        <p>The elements in this list are no longer documented because
        they were either:</p>
        <ul style="list-style-type: circle">
            <li>removed entirely</li>
            <li>went from public or protected to private or default visibility</li>
            <li>no longer part of the published API through javadoc tags</li>
        </ul>
        </font>

        <xsl:choose>
            <xsl:when test="line">
                <ul>
                   <xsl:apply-templates/>
                </ul>
            </xsl:when>
            <xsl:otherwise>
                <blockquote><i>none</i></blockquote>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="added">
        <h3>Newly Published:</h3>

        <font color="gray" size="-1">
        <p>The elements in this list are new to documentation because
        they were either:</p>
        <ul style="list-style-type: circle">
            <li>addded recently</li>
            <li>went from private or default visibility to public or protected</li>
            <li>have been made part of the published API through javadoc tags</li>
        </ul>
        </font>

        <xsl:choose>
            <xsl:when test="line">
                <ul>
                   <xsl:apply-templates/>
                </ul>
            </xsl:when>
            <xsl:otherwise>
                <blockquote><i>none</i></blockquote>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="line">
        <li><nobr><code><xsl:value-of select="."/></code></nobr></li>
    </xsl:template>

    <xsl:template match="*"/>

</xsl:stylesheet>
