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

    <xsl:template match="differences">
	<html>

	<head>
	    <title>API Change History - Version <xsl:value-of select="old"/> to Version <xsl:value-of select="new"/></title>
	</head>

	<body bgcolor="#ffffff">

	<h1>API Change History - Version <xsl:value-of select="old"/> to Version <xsl:value-of select="new"/></h1>

	<xsl:apply-templates/>

	</body>

	</html>
    </xsl:template>

    <xsl:template match="removed-packages">
	<h2>Removed Packages:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="removed-interfaces">
	<h2>Removed Interfaces:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="removed-classes">
	<h2>Removed Classes:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="deprecated-interfaces">
	<h2>Newly Deprecated Interfaces:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="deprecated-classes">
	<h2>Newly Deprecated Classes:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="modified-interfaces">
	<h2>Modified Interfaces:</h2>
	<blockquote>
	<xsl:apply-templates/>
	</blockquote>
    </xsl:template>
 
    <xsl:template match="modified-classes">
	<h2>Modified Classes:</h2>
	<blockquote>
	<xsl:apply-templates/>
	</blockquote>
    </xsl:template>
 
    <xsl:template match="undeprecated-interfaces">
	<h2>Formerly Deprecated Interfaces:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="undeprecated-classes">
	<h2>Formerly Deprecated Classes:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="new-packages">
	<h2>New Packages:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="new-interfaces">
	<h2>New Interfaces:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="new-classes">
	<h2>New Classes:</h2>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="class">
	<h3><code><xsl:value-of select="name"/></code></h3>
	<blockquote>
	<xsl:if test="old-declaration">
	    <h4>Declaration Changes:</h4>
	    <p><nobr><code>
		<b>old:</b> <xsl:value-of select="old-declaration"/><br/>
		<b>new:</b> <xsl:value-of select="new-declaration"/>
	    </code></nobr></p>
	</xsl:if>
	<xsl:apply-templates/>
	</blockquote>
    </xsl:template>

    <xsl:template match="removed-fields">
	<h4>Removed Fields:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="removed-constructors">
	<h4>Removed Constructors:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="removed-methods">
	<h4>Removed Methods:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="deprecated-fields">
	<h4>Newly Deprecated Fields:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="deprecated-constructors">
	<h4>Newly Deprecated Constructors:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="deprecated-methods">
	<h4>Newly Deprecated Methods:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="modified-fields">
	<h4>Field Declaration Changes:</h4>
	<blockquote>
	<xsl:apply-templates/>
	</blockquote>
    </xsl:template>
 
    <xsl:template match="modified-constructors">
	<h4>Constructor Declaration Changes:</h4>
	<blockquote>
	<xsl:apply-templates/>
	</blockquote>
    </xsl:template>
 
    <xsl:template match="modified-methods">
	<h4>Method Declaration Changes:</h4>
	<blockquote>
	<xsl:apply-templates/>
	</blockquote>
    </xsl:template>
 
    <xsl:template match="undeprecated-fields">
	<h4>Formerly Deprecated Fields:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="undeprecated-constructors">
	<h4>Formerly Deprecated Constructors:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="undeprecated-methods">
	<h4>Formerly Deprecated Methods:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="new-fields">
	<h4>New Fields:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="new-constructors">
	<h4>New Constructors:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="new-methods">
	<h4>New Methods:</h4>
	<ul>
	    <xsl:apply-templates/>
	</ul>
    </xsl:template>
 
    <xsl:template match="feature">
	<xsl:if test="old-declaration">
	    <p><nobr><code>
		<b>old:</b> <xsl:value-of select="old-declaration"/><br/>
		<b>new:</b> <xsl:value-of select="new-declaration"/>
	    </code></nobr></p>
	</xsl:if>
	<xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="removed-packages/name | removed-interfaces/name | removed-classes/name | deprecated-interfaces/name | deprecated-classes/name | undeprecated-interfaces/name | undeprecated-classes/name | new-packages/name | new-interfaces/name | new-classes/name | removed-fields/declaration | removed-constructors/declaration | removed-methods/declaration | deprecated-fields/declaration | deprecated-constructors/declaration | deprecated-methods/declaration | undeprecated-fields/declaration | undeprecated-constructors/declaration | undeprecated-methods/declaration | new-fields/declaration | new-constructors/declaration | new-methods/declaration">
	<li><nobr><code><xsl:value-of select="."/></code></nobr></li>
    </xsl:template>
 
    <xsl:template match="*"></xsl:template>
</xsl:stylesheet>
