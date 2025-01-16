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
        <xsl:text>strict digraph {
</xsl:text>
        <xsl:apply-templates select="package"/>

        <xsl:text>
    // Dependencies
    edge [style = dotted; arrowhead = vee]

</xsl:text>

        <xsl:apply-templates select="//outbound | //inbound"/>

        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <xsl:template match="package">
        <xsl:text>
    subgraph "cluster_package_</xsl:text><xsl:value-of select="name"/><xsl:if test="string-length(name) = 0">default</xsl:if><xsl:text>" {
        style = invisible
        edge [dir = none]

        "</xsl:text><xsl:value-of select="name"/><xsl:if test="string-length(name) = 0">default</xsl:if><xsl:text>" [label = </xsl:text>
        <xsl:choose>
            <xsl:when test="string-length(name) > 0"><xsl:text>"</xsl:text><xsl:value-of select="name/@simple-name"/><xsl:text>"</xsl:text></xsl:when>
            <xsl:otherwise><xsl:text>&lt;&lt;i&gt;default&lt;/i&gt;&gt;</xsl:text></xsl:otherwise>
        </xsl:choose>
        <xsl:if test="boolean(@confirmed = 'no')"><xsl:text>; color = grey; style = filled; fillcolor = "#e7e7e7"</xsl:text></xsl:if><xsl:text>]</xsl:text>
        <xsl:if test="class">
            <xsl:text>
        subgraph "cluster_classes_in_</xsl:text><xsl:value-of select="name"/><xsl:if test="string-length(name) = 0">default</xsl:if><xsl:text>" {
            style = invisible
            edge [dir = none]
</xsl:text>
            <xsl:apply-templates select="class"/>
            <xsl:text>
        }
</xsl:text>
        </xsl:if>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="class">
        <xsl:text>
            "</xsl:text><xsl:value-of select="name"/><xsl:text>" [label = "</xsl:text><xsl:value-of select="name/@simple-name"/><xsl:text>"</xsl:text><xsl:if test="boolean(@confirmed = 'no')"><xsl:text>; color = grey; style = filled; fillcolor = "#e7e7e7"</xsl:text></xsl:if><xsl:text>]
            "</xsl:text><xsl:value-of select="../name"/><xsl:if test="string-length(../name) = 0">default</xsl:if><xsl:text>" -&gt; "</xsl:text><xsl:value-of select="name"/><xsl:text>"</xsl:text><xsl:if test="boolean(@confirmed = 'no')"><xsl:text> [color = grey]</xsl:text></xsl:if>
        <xsl:if test="feature">
            <xsl:text>
            subgraph "cluster_features_in_</xsl:text><xsl:value-of select="name"/><xsl:text>" {
                style = invisible
                edge [dir = none]
</xsl:text>
                <xsl:apply-templates select="feature"/>
                <xsl:text>
            }</xsl:text>
        </xsl:if>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="feature">
        <xsl:text>
                "</xsl:text><xsl:value-of select="name"/><xsl:text>" [label = "</xsl:text><xsl:value-of select="name/@simple-name"/><xsl:text>"</xsl:text><xsl:if test="boolean(@confirmed = 'no')"><xsl:text>; color = grey; style = filled; fillcolor = "#e7e7e7"</xsl:text></xsl:if><xsl:text>]
                "</xsl:text><xsl:value-of select="../name"/><xsl:text>" -&gt; "</xsl:text><xsl:value-of select="name"/><xsl:text>"</xsl:text><xsl:if test="boolean(@confirmed = 'no')"><xsl:text> [color = grey]</xsl:text></xsl:if><xsl:text>

</xsl:text>
    </xsl:template>

    <xsl:template match="outbound">
        <xsl:text>    "</xsl:text><xsl:value-of select="../name"/><xsl:if test="string-length(../name) = 0">default</xsl:if><xsl:text>" -&gt; "</xsl:text><xsl:value-of select="."/><xsl:if test="string-length(.) = 0">default</xsl:if><xsl:text>"
</xsl:text>
    </xsl:template>

    <xsl:template match="inbound">
        <xsl:text>    "</xsl:text><xsl:value-of select="."/><xsl:if test="string-length(.) = 0">default</xsl:if><xsl:text>" -&gt; "</xsl:text><xsl:value-of select="../name"/><xsl:if test="string-length(../name) = 0">default</xsl:if><xsl:text>"
</xsl:text>
    </xsl:template>

</xsl:stylesheet>
