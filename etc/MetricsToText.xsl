<?xml version="1.0"?> 

<!--
    Copyright (c) 2001-2025, Jean Tessier
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
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Project Metrics-->

    <xsl:template match="project">
        <xsl:value-of select="name"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="measurement"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="group"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement">
        <xsl:text>    </xsl:text><xsl:value-of select="long-name"/>: <xsl:value-of select="value"/>
        <xsl:if test="minimum"><xsl:text> </xsl:text>[<xsl:value-of select="minimum"/>, <xsl:value-of select="median"/>, <xsl:value-of select="average"/>, <xsl:value-of select="standard-deviation"/>, <xsl:value-of select="maximum"/>, <xsl:value-of select="sum"/>, <xsl:value-of select="nb-data-points"/>]</xsl:if>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="members"/>
    </xsl:template>

    <xsl:template match="project/measurement/members/member">
        <xsl:text>        </xsl:text><xsl:value-of select="text()"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Group Metrics-->

    <xsl:template match="group">
        <xsl:text>    </xsl:text><xsl:value-of select="name"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="measurement"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="class"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement">
        <xsl:text>        </xsl:text><xsl:value-of select="long-name"/>: <xsl:value-of select="value"/>
        <xsl:if test="minimum"><xsl:text> </xsl:text>[<xsl:value-of select="minimum"/>, <xsl:value-of select="median"/>, <xsl:value-of select="average"/>, <xsl:value-of select="standard-deviation"/>, <xsl:value-of select="maximum"/>, <xsl:value-of select="sum"/>, <xsl:value-of select="nb-data-points"/>]</xsl:if>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="members"/>
    </xsl:template>

    <xsl:template match="group/measurement/members/member">
        <xsl:text>            </xsl:text><xsl:value-of select="text()"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Class Metrics-->

    <xsl:template match="class">
        <xsl:text>        </xsl:text><xsl:value-of select="name"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="measurement"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="method"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement">
        <xsl:text>            </xsl:text><xsl:value-of select="long-name"/>: <xsl:value-of select="value"/>
        <xsl:if test="minimum"><xsl:text> </xsl:text>[<xsl:value-of select="minimum"/>, <xsl:value-of select="median"/>, <xsl:value-of select="average"/>, <xsl:value-of select="standard-deviation"/>, <xsl:value-of select="maximum"/>, <xsl:value-of select="sum"/>, <xsl:value-of select="nb-data-points"/>]</xsl:if>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="members"/>
    </xsl:template>

    <xsl:template match="class/measurement/members/member">
        <xsl:text>                </xsl:text><xsl:value-of select="text()"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Method Metrics-->

    <xsl:template match="method">
        <xsl:text>            </xsl:text><xsl:value-of select="name"/>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="measurement"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement">
        <xsl:text>                </xsl:text><xsl:value-of select="long-name"/>: <xsl:value-of select="value"/>
        <xsl:if test="minimum"><xsl:text> </xsl:text>[<xsl:value-of select="minimum"/>, <xsl:value-of select="median"/>, <xsl:value-of select="average"/>, <xsl:value-of select="standard-deviation"/>, <xsl:value-of select="maximum"/>, <xsl:value-of select="sum"/>, <xsl:value-of select="nb-data-points"/>]</xsl:if>
        <xsl:text>
</xsl:text>
        <xsl:apply-templates select="members"/>
    </xsl:template>

    <xsl:template match="method/measurement/members/member">
        <xsl:text>                    </xsl:text><xsl:value-of select="text()"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

</xsl:stylesheet>
