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
        <xsl:text>metrics:
</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Project Metrics-->

    <xsl:template match="project">
        <xsl:text>  - name: </xsl:text><xsl:apply-templates select="name"/>
        <xsl:text>
</xsl:text>

        <xsl:choose>
            <xsl:when test="measurement">
                <xsl:text>    measurements:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="measurement"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>    measurements: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="group">
                <xsl:text>    groups:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="group"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>    groups: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="project/measurement">
        <xsl:text>      -
</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="project/measurement/short-name">
        <xsl:text>        short-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/long-name">
        <xsl:text>        long-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/value">
        <xsl:text>        value: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/minimum">
        <xsl:text>        minimum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/median">
        <xsl:text>        median: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/average">
        <xsl:text>        average: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/standard-deviation">
        <xsl:text>        standard-deviation: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/maximum">
        <xsl:text>        maximum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/sum">
        <xsl:text>        sum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/nb-data-points">
        <xsl:text>        nb-data-points: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="project/measurement/members">
        <xsl:choose>
            <xsl:when test="member">
                <xsl:text>        members:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="member"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>        members: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="project/measurement/members/member">
        <xsl:text>          - </xsl:text><xsl:choose><xsl:when test="text()"><xsl:value-of select="text()"/></xsl:when><xsl:otherwise>""</xsl:otherwise></xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Group Metrics-->

    <xsl:template match="group">
        <xsl:text>      - name: </xsl:text><xsl:apply-templates select="name"/>
        <xsl:text>
</xsl:text>

        <xsl:choose>
            <xsl:when test="measurement">
                <xsl:text>        measurements:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="measurement"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>        measurements: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="class">
                <xsl:text>        classes:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="class"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>        classes: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="group/measurement">
        <xsl:text>          -
</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="group/measurement/short-name">
        <xsl:text>            short-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/long-name">
        <xsl:text>            long-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/value">
        <xsl:text>            value: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/minimum">
        <xsl:text>            minimum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/median">
        <xsl:text>            median: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/average">
        <xsl:text>            average: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/standard-deviation">
        <xsl:text>            standard-deviation: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/maximum">
        <xsl:text>            maximum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/sum">
        <xsl:text>            sum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/nb-data-points">
        <xsl:text>            nb-data-points: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="group/measurement/members">
        <xsl:choose>
            <xsl:when test="member">
                <xsl:text>            members:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="member"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>            members: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="group/measurement/members/member">
        <xsl:text>              - </xsl:text><xsl:choose><xsl:when test="text()"><xsl:value-of select="text()"/></xsl:when><xsl:otherwise>""</xsl:otherwise></xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Class Metrics-->

    <xsl:template match="class">
        <xsl:text>          - name: </xsl:text><xsl:apply-templates select="name"/>
        <xsl:text>
</xsl:text>

        <xsl:choose>
            <xsl:when test="measurement">
                <xsl:text>            measurements:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="measurement"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>            measurements: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="method">
                <xsl:text>            methods:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="method"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>            methods: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="class/measurement">
        <xsl:text>              -
</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="class/measurement/short-name">
        <xsl:text>                short-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/long-name">
        <xsl:text>                long-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/value">
        <xsl:text>                value: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/minimum">
        <xsl:text>                minimum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/median">
        <xsl:text>                median: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/average">
        <xsl:text>                average: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/standard-deviation">
        <xsl:text>                standard-deviation: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/maximum">
        <xsl:text>                maximum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/sum">
        <xsl:text>                sum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/nb-data-points">
        <xsl:text>                nb-data-points: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="class/measurement/members">
        <xsl:choose>
            <xsl:when test="member">
                <xsl:text>                members:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="member"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>                members: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="class/measurement/members/member">
        <xsl:text>                  - </xsl:text><xsl:choose><xsl:when test="text()"><xsl:value-of select="text()"/></xsl:when><xsl:otherwise>""</xsl:otherwise></xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Method Metrics-->

    <xsl:template match="method">
        <xsl:text>              - name: </xsl:text><xsl:apply-templates select="name"/>
        <xsl:text>
</xsl:text>

        <xsl:choose>
            <xsl:when test="measurement">
                <xsl:text>                measurements:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="measurement"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>                measurements: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="method/measurement">
        <xsl:text>                  -
</xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="method/measurement/short-name">
        <xsl:text>                    short-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/long-name">
        <xsl:text>                    long-name: </xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/value">
        <xsl:text>                    value: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/minimum">
        <xsl:text>                    minimum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/median">
        <xsl:text>                    median: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/average">
        <xsl:text>                    average: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/standard-deviation">
        <xsl:text>                    standard-deviation: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/maximum">
        <xsl:text>                    maximum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/sum">
        <xsl:text>                    sum: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/nb-data-points">
        <xsl:text>                    nb-data-points: </xsl:text>
        <xsl:choose>
            <xsl:when test="text() = 'NaN'"><xsl:text>.NaN</xsl:text></xsl:when>
            <xsl:when test="text() = 'Infinity'"><xsl:text>.Inf</xsl:text></xsl:when>
            <xsl:when test="text() = '-Infinity'"><xsl:text>-.Inf</xsl:text></xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="method/measurement/members">
        <xsl:choose>
            <xsl:when test="member">
                <xsl:text>                    members:</xsl:text>
                <xsl:text>
</xsl:text>
                <xsl:apply-templates select="member"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>                    members: []</xsl:text>
                <xsl:text>
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="method/measurement/members/member">
        <xsl:text>                      - </xsl:text><xsl:choose><xsl:when test="text()"><xsl:value-of select="text()"/></xsl:when><xsl:otherwise>""</xsl:otherwise></xsl:choose>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Helpers -->

    <xsl:template match="name">
        <xsl:choose>
            <xsl:when test="text()"><xsl:value-of select="text()"/></xsl:when>
            <xsl:otherwise>""</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
