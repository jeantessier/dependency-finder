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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="text"/>

    <xsl:template match="differences">
	<xsl:apply-templates select="modified-interfaces/class"/>
	<xsl:apply-templates select="modified-classes/class"/>
    </xsl:template>
 
    <xsl:template match="class[modified-declaration[(old-declaration/@extends!=new-declaration/@extends) or (old-declaration/@implements!=new-declaration/@implements)]]">
	- <xsl:value-of select="name"/>
    </xsl:template>
 
    <xsl:template match="*"></xsl:template>

</xsl:stylesheet>

