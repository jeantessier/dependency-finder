<?xml version="1.0"?> 

<!--
    Copyright (c) 2001-2023, Jean Tessier
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

    <xsl:template match="metrics">
        <xsl:text>[</xsl:text>
        <xsl:for-each select="project"><xsl:apply-templates select="."/><xsl:if test="following-sibling::project"><xsl:text>,</xsl:text></xsl:if></xsl:for-each>
        <xsl:text>]</xsl:text>
    </xsl:template>

    <xsl:template match="project">
        <xsl:text>{</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"measurements":[</xsl:text><xsl:for-each select="measurement"><xsl:apply-templates select="."/><xsl:if test="following-sibling::measurement"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"groups":[</xsl:text><xsl:for-each select="group"><xsl:apply-templates select="."/><xsl:if test="following-sibling::group"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="group">
        <xsl:text>{</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"measurements":[</xsl:text><xsl:for-each select="measurement"><xsl:apply-templates select="."/><xsl:if test="following-sibling::measurement"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"classes":[</xsl:text><xsl:for-each select="class"><xsl:apply-templates select="."/><xsl:if test="following-sibling::class"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="class">
        <xsl:text>{</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"measurements":[</xsl:text><xsl:for-each select="measurement"><xsl:apply-templates select="."/><xsl:if test="following-sibling::measurement"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>],</xsl:text>
        <xsl:text>"methods":[</xsl:text><xsl:for-each select="method"><xsl:apply-templates select="."/><xsl:if test="following-sibling::method"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="method">
        <xsl:text>{</xsl:text>
        <xsl:text>"name":"</xsl:text><xsl:value-of select="name"/><xsl:text>",</xsl:text>
        <xsl:text>"measurements":[</xsl:text><xsl:for-each select="measurement"><xsl:apply-templates select="."/><xsl:if test="following-sibling::measurement"><xsl:text>,</xsl:text></xsl:if></xsl:for-each><xsl:text>]</xsl:text>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="measurement">
        <xsl:text>{</xsl:text>
        <xsl:for-each select="*"><xsl:apply-templates select="."/><xsl:if test="following-sibling::*"><xsl:text>,</xsl:text></xsl:if></xsl:for-each>
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template match="short-name">
        <xsl:text>"short-name":"</xsl:text><xsl:value-of select="."/><xsl:text>"</xsl:text>
    </xsl:template>

    <xsl:template match="long-name">
        <xsl:text>"long-name":"</xsl:text><xsl:value-of select="."/><xsl:text>"</xsl:text>
    </xsl:template>

    <xsl:template match="value">
        <xsl:text>"value":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="minimum">
        <xsl:text>"minimum":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="median">
        <xsl:text>"median":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="average">
        <xsl:text>"average":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="standard-deviation">
        <xsl:text>"standard-deviation":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="maximum">
        <xsl:text>"maximum":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="sum">
        <xsl:text>"sum":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="nb-data-points">
        <xsl:text>"nb-data-points":</xsl:text><xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="members">
        <xsl:text>"members":[</xsl:text>
        <xsl:for-each select="member"><xsl:text>"</xsl:text><xsl:apply-templates select="."/><xsl:text>"</xsl:text><xsl:if test="following-sibling::member"><xsl:text>,</xsl:text></xsl:if></xsl:for-each>
        <xsl:text>]</xsl:text>
    </xsl:template>

</xsl:stylesheet>
