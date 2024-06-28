<?xml version="1.0"?> 

<!--
    Copyright (c) 2001-2024, Jean Tessier
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

    <xsl:output method="text"/>
    <xsl:strip-space elements="*"/> 

    <xsl:template match="dependencies">
        <xsl:text>[</xsl:text>
        <xsl:for-each select="package"><xsl:apply-templates select="."/><xsl:if test="following-sibling::package"><xsl:text>,</xsl:text></xsl:if></xsl:for-each>
        <xsl:text>]</xsl:text>
    </xsl:template>

    <xsl:template match="package">
        <xsl:text>{</xsl:text>
        <xsl:text>"type":"package",</xsl:text>
        <xsl:text>"confirmed":"</xsl:text><xsl:value-of select="boolean(@confirmed = 'yes')"/><xsl:text>",</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"outbound":[</xsl:text><xsl:for-each select="outbound"><xsl:apply-templates select="."/><xsl:if test="following-sibling::outbound"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"inbound":[</xsl:text><xsl:for-each select="inbound"><xsl:apply-templates select="."/><xsl:if test="following-sibling::inbound"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"classes":[</xsl:text><xsl:for-each select="class"><xsl:apply-templates select="."/><xsl:if test="following-sibling::class"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="class">
        <xsl:text>{</xsl:text>
        <xsl:text>"type":"class",</xsl:text>
        <xsl:text>"confirmed":"</xsl:text><xsl:value-of select="boolean(@confirmed = 'yes')"/><xsl:text>",</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"outbound":[</xsl:text><xsl:for-each select="outbound"><xsl:apply-templates select="."/><xsl:if test="following-sibling::outbound"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"inbound":[</xsl:text><xsl:for-each select="inbound"><xsl:apply-templates select="."/><xsl:if test="following-sibling::inbound"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"features":[</xsl:text><xsl:for-each select="feature"><xsl:apply-templates select="."/><xsl:if test="following-sibling::feature"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="feature">
        <xsl:text>{</xsl:text>
        <xsl:text>"type":"feature",</xsl:text>
        <xsl:text>"confirmed":"</xsl:text><xsl:value-of select="boolean(@confirmed = 'yes')"/><xsl:text>",</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"outbound":[</xsl:text><xsl:for-each select="outbound"><xsl:apply-templates select="."/><xsl:if test="following-sibling::outbound"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"inbound":[</xsl:text><xsl:for-each select="inbound"><xsl:apply-templates select="."/><xsl:if test="following-sibling::inbound"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="outbound | inbound">
        <xsl:text>{</xsl:text>
        <xsl:text>"type":"</xsl:text><xsl:value-of select="@type"/><xsl:text>",</xsl:text>
        <xsl:text>"confirmed":"</xsl:text><xsl:value-of select="boolean(@confirmed = 'yes')"/><xsl:text>",</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="."/><xsl:text>"</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

</xsl:stylesheet>
