<?xml version="1.0"?> 

<!--
    Copyright (c) 2001-2002, Jean Tessier
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

    <xsl:output method="html" indent="yes"/>
    <xsl:strip-space elements="*"/> 

    <xsl:template match="differences">
	<html>

	<head>
	    <title><xsl:if test="name/text()"><xsl:value-of select="name"/> - </xsl:if>Documentation Changes</title>
	</head>

	<body bgcolor="#ffffff">

	<h1><xsl:if test="name/text()"><xsl:value-of select="name"/> - </xsl:if>Documentation Changes</h1>

	<h1><xsl:value-of select="old"/> to <xsl:value-of select="new"/></h1>

	<h2>No Longer Documented:</h2>
	<ul>
	    <xsl:apply-templates select="descendant::undocumented-packages/name |
					 descendant::undocumented-interfaces/name |
					 descendant::undocumented-classes/name |
					 descendant::undocumented-fields/declaration |
					 descendant::undocumented-constructors/declaration |
					 descendant::undocumented-methods/declaration"/>
	</ul>

	<h2>Now Documented:</h2>
	<ul>
	    <xsl:apply-templates select="descendant::documented-packages/name |
					 descendant::documented-interfaces/name |
					 descendant::documented-classes/name |
					 descendant::documented-fields/declaration |
					 descendant::documented-constructors/declaration |
					 descendant::documented-methods/declaration"/>
	</ul>

	</body>

	</html>
    </xsl:template>

    <xsl:template match="name">
	<li><nobr><code><xsl:value-of select="."/></code></nobr></li>
    </xsl:template>

    <xsl:template match="declaration">
	<li><nobr><code><xsl:value-of select="@full-signature"/></code></nobr></li>
    </xsl:template>

    <xsl:template match="*"></xsl:template>

</xsl:stylesheet>
