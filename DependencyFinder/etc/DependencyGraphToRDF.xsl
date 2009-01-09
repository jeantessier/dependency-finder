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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">

    <xsl:strip-space elements="*"/> 

    <xsl:template match="dependencies">
        <rdf:RDF
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
            xmlns="http://www.fi.muni.cz#"
            xml:base="http://www.fi.muni.cz">

            <xsl:apply-templates/>

        </rdf:RDF>
    </xsl:template>

    <xsl:template match="package">
        <package rdf:ID="{name}">
            <name><xsl:value-of select="name"/></name>
            <xsl:apply-templates select="outbound | inbound"/>
            <xsl:apply-templates select="class"/>
        </package>
    </xsl:template>

    <xsl:template match="class">
        <xsl:variable name="localname">
            <xsl:call-template name="getname">
                <xsl:with-param name="name" select="name"/>
            </xsl:call-template>
        </xsl:variable>
        <classes>
            <class rdf:ID="{name}">
                <name><xsl:value-of select="$localname"/></name>
                <xsl:apply-templates select="outbound | inbound"/>
                <xsl:apply-templates select="feature"/>
            </class>
        </classes>
    </xsl:template>

    <xsl:template match="feature">
        <xsl:variable name="newname" select="substring-before(name,'(')"/>
        <xsl:variable name="params" select="substring-after(name,'(')"/>
        <xsl:variable name="localname">
            <xsl:if test="$newname=''">
                <xsl:call-template name="getname">
                    <xsl:with-param name="name" select="name"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="$newname!=''">
                <xsl:call-template name="getname">
                    <xsl:with-param name="name" select="$newname"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:variable>
        <features>
            <feature rdf:ID="{name}">
                <name>
                    <xsl:value-of select="$localname"/>
                    <xsl:if test="$params!=''">(</xsl:if>
                    <xsl:value-of select="$params"/>
                </name>
                <xsl:apply-templates select="outbound | inbound"/>
            </feature>
        </features>
    </xsl:template>

    <xsl:template match="inbound">
        <xsl:if test='@type="feature"'>
            <inbound_feature rdf:resource="#{name}"/>
        </xsl:if>
        <xsl:if test='@type="class"'>
            <inbound_class rdf:resource="#{name}"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="outbound">
        <xsl:if test='@type="feature"'>
            <outbound_feature rdf:resource="#{name}"/>
        </xsl:if>
        <xsl:if test='@type="class"'>
            <outbound_class rdf:resource="#{name}"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="getname">
        <xsl:param name="name"/>
        <xsl:variable name="name1">
            <xsl:call-template name="reverse">
                <xsl:with-param name="theString" select="$name"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="name2" select="substring-before($name1,'.')"/>
        <xsl:variable name="name3">
            <xsl:call-template name="reverse">
                <xsl:with-param name="theString" select="$name2"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$name3"/>
    </xsl:template>

    <xsl:template name="reverse">
        <xsl:param name="theString"/>
        <xsl:variable name="thisLength" select="string-length($theString)"/>
        <xsl:choose>
            <xsl:when test="$thisLength = 1">
                <xsl:value-of select="$theString"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="restReverse">
                    <xsl:call-template name="reverse">
                        <xsl:with-param name="theString" select="substring($theString, 1, $thisLength -1)"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:value-of select="concat(substring($theString,
                                                       $thisLength,
                                                       1),
                                             $restReverse)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
