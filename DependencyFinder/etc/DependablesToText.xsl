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

    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/> 

    <xsl:template match="dependencies">
	<xsl:apply-templates/>
    </xsl:template>
  
    <xsl:template match="package[inbound | class/inbound | class/feature/inbound]">
	<xsl:value-of select="name"/><xsl:text>
</xsl:text>
	<xsl:for-each select="inbound">
	    <xsl:text disable-output-escaping="yes">    &lt;-- </xsl:text><xsl:value-of select="."/><xsl:text>
</xsl:text>
	</xsl:for-each>
	<xsl:apply-templates select="class"/>
    </xsl:template>
    <xsl:template match="package"></xsl:template>
  
    <xsl:template match="class[inbound | feature/inbound]">
	<xsl:text>    </xsl:text><xsl:value-of select="name"/><xsl:text>
</xsl:text>
	<xsl:for-each select="inbound">
	    <xsl:text disable-output-escaping="yes">        &lt;-- </xsl:text><xsl:value-of select="."/><xsl:text>
</xsl:text>
	</xsl:for-each>
	<xsl:apply-templates select="feature"/>
    </xsl:template>
    <xsl:template match="class"></xsl:template>
  
    <xsl:template match="feature[inbound]">
	<xsl:text>        </xsl:text><xsl:value-of select="name"/><xsl:text>
</xsl:text>
	<xsl:for-each select="inbound">
	    <xsl:text disable-output-escaping="yes">            &lt;-- </xsl:text><xsl:value-of select="."/><xsl:text>
</xsl:text>
	</xsl:for-each>
    </xsl:template>
    <xsl:template match="feature"></xsl:template>
  
</xsl:stylesheet>
