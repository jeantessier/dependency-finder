<?xml version="1.0"?> 

<!--
  --  Dependency Finder - Computes quality factors from compiled Java code
  --  Copyright (C) 2001  Jean Tessier
  --  
  --  This library is free software; you can redistribute it and/or
  --  modify it under the terms of the GNU Lesser General Public
  --  License as published by the Free Software Foundation; either
  --  version 2.1 of the License, or (at your option) any later version.
  --  
  --  This library is distributed in the hope that it will be useful,
  --  but WITHOUT ANY WARRANTY; without even the implied warranty of
  --  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  --  Lesser General Public License for more details.
  --  
  --  You should have received a copy of the GNU Lesser General Public
  --  License along with this library; if not, write to the Free Software
  --  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" indent="yes"/>
    <xsl:strip-space elements="*"/> 

    <xsl:template match="dependencies">
	<html>

	<head>
	</head>

	<body bgcolor="#ffffff">
	<code>
	    <xsl:apply-templates/>
	</code>
	</body>

	</html>
    </xsl:template>
  
    <xsl:template match="package">
	<xsl:variable name="name"><xsl:apply-templates select="name"/></xsl:variable>
	<h1><a name="{$name}"><xsl:value-of select="$name"/></a></h1>
	<blockquote>
	<xsl:apply-templates select="outbound"/>
	<xsl:apply-templates select="class"/>
	</blockquote>
    </xsl:template>
  
    <xsl:template match="class">
	<xsl:variable name="name"><xsl:apply-templates select="name"/></xsl:variable>
	<h2><a name="{$name}"><xsl:value-of select="$name"/></a></h2>
	<blockquote>
	<xsl:apply-templates select="outbound"/>
	<xsl:apply-templates select="feature"/>
	</blockquote>
    </xsl:template>
  
    <xsl:template match="feature">
	<xsl:variable name="name"><xsl:apply-templates select="name"/></xsl:variable>
	<h3><a name="{$name}"><xsl:value-of select="$name"/></a></h3>
	<blockquote>
	<xsl:apply-templates select="outbound"/>
	</blockquote>
    </xsl:template>
  
    <xsl:template match="outbound">
	<xsl:variable name="name"><xsl:value-of select="."/></xsl:variable>
	--&gt; <a href="#{$name}"><xsl:value-of select="$name"/></a><br/>
    </xsl:template>
  
</xsl:stylesheet>
