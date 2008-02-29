<?xml version="1.0"?> 

<!--
    Copyright (c) 2001-2008, Jean Tessier
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

    <xsl:output method="xml"/>

    <xsl:template match="dependencies">
        <graphml
            xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:y="http://www.yworks.com/xml/graphml"
            xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd">

            <graph id="" edgedefault="directed">
                <xsl:apply-templates/>
            </graph>

        </graphml>
    </xsl:template>
  
    <xsl:template match="package | class | feature">
        <xsl:variable name="name"><xsl:apply-templates select="name"/></xsl:variable>
        <node id="{$name}"></node>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="inbound">
        <xsl:variable name="source"><xsl:value-of select="."/></xsl:variable>
        <xsl:variable name="target"><xsl:value-of select="../name"/></xsl:variable>
        <edge source="{$source}" target="{$target}"/>
    </xsl:template>
  
    <xsl:template match="outbound">
        <!--
        <xsl:variable name="source"><xsl:value-of select="../name"/></xsl:variable>
        <xsl:variable name="target"><xsl:value-of select="."/></xsl:variable>
        <edge source="{$source}" target="{$target}"/>
        -->
    </xsl:template>

    <xsl:template match="name"/>

</xsl:stylesheet>
