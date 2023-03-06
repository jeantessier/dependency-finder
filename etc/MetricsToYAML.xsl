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
        <xsl:text>      - short-name: </xsl:text><xsl:value-of select="short-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>        long-name: </xsl:text><xsl:value-of select="long-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>        value: </xsl:text><xsl:value-of select="value"/>
        <xsl:text>
</xsl:text>

        <xsl:if test="minimum">
            <xsl:text>        minimum: </xsl:text><xsl:value-of select="minimum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>        median: </xsl:text><xsl:value-of select="median"/>
            <xsl:text>
</xsl:text>
            <xsl:text>        average: </xsl:text><xsl:value-of select="average"/>
            <xsl:text>
</xsl:text>
            <xsl:text>        standard-deviation: </xsl:text><xsl:value-of select="standard-deviation"/>
            <xsl:text>
</xsl:text>
            <xsl:text>        maximum: </xsl:text><xsl:value-of select="maximum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>        sum: </xsl:text><xsl:value-of select="sum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>        nb-data-points: </xsl:text><xsl:value-of select="nb-data-points"/>
            <xsl:text>
</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="members"/>
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
        <xsl:text>          - short-name: </xsl:text><xsl:value-of select="short-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>            long-name: </xsl:text><xsl:value-of select="long-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>            value: </xsl:text><xsl:value-of select="value"/>
        <xsl:text>
</xsl:text>

        <xsl:if test="minimum">
            <xsl:text>            minimum: </xsl:text><xsl:value-of select="minimum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>            median: </xsl:text><xsl:value-of select="median"/>
            <xsl:text>
</xsl:text>
            <xsl:text>            average: </xsl:text><xsl:value-of select="average"/>
            <xsl:text>
</xsl:text>
            <xsl:text>            standard-deviation: </xsl:text><xsl:value-of select="standard-deviation"/>
            <xsl:text>
</xsl:text>
            <xsl:text>            maximum: </xsl:text><xsl:value-of select="maximum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>            sum: </xsl:text><xsl:value-of select="sum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>            nb-data-points: </xsl:text><xsl:value-of select="nb-data-points"/>
            <xsl:text>
</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="members"/>
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
        <xsl:text>              - short-name: </xsl:text><xsl:value-of select="short-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>                long-name: </xsl:text><xsl:value-of select="long-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>                value: </xsl:text><xsl:value-of select="value"/>
        <xsl:text>
</xsl:text>

        <xsl:if test="minimum">
            <xsl:text>                minimum: </xsl:text><xsl:value-of select="minimum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                median: </xsl:text><xsl:value-of select="median"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                average: </xsl:text><xsl:value-of select="average"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                standard-deviation: </xsl:text><xsl:value-of select="standard-deviation"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                maximum: </xsl:text><xsl:value-of select="maximum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                sum: </xsl:text><xsl:value-of select="sum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                nb-data-points: </xsl:text><xsl:value-of select="nb-data-points"/>
            <xsl:text>
</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="members"/>
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
        <xsl:text>                  - short-name: </xsl:text><xsl:value-of select="short-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>                    long-name: </xsl:text><xsl:value-of select="long-name"/>
        <xsl:text>
</xsl:text>
        <xsl:text>                    value: </xsl:text><xsl:value-of select="value"/>
        <xsl:text>
</xsl:text>

        <xsl:if test="minimum">
            <xsl:text>                    minimum: </xsl:text><xsl:value-of select="minimum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                    median: </xsl:text><xsl:value-of select="median"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                    average: </xsl:text><xsl:value-of select="average"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                    standard-deviation: </xsl:text><xsl:value-of select="standard-deviation"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                    maximum: </xsl:text><xsl:value-of select="maximum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                    sum: </xsl:text><xsl:value-of select="sum"/>
            <xsl:text>
</xsl:text>
            <xsl:text>                    nb-data-points: </xsl:text><xsl:value-of select="nb-data-points"/>
            <xsl:text>
</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="members"/>
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
