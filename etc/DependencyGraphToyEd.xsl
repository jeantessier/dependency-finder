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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:y="http://www.yworks.com/xml/graphml">

    <xsl:strip-space elements="*"/>

    <xsl:template match="dependencies">
        <graphml
            xmlns="http://graphml.graphdrawing.org/xmlns/graphml"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:y="http://www.yworks.com/xml/graphml"
            xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd">

            <key for="node" id="d0" yfiles.type="nodegraphics"/>
            <key for="edge" id="d1" yfiles.type="edgegraphics"/>
            <graph edgedefault="directed">
                <xsl:apply-templates/>
            </graph>

        </graphml>
    </xsl:template>

    <xsl:template match="package[@confirmed='no']">
        <node id="{name}">
            <data key="d0">
                <y:ShapeNode>
                    <y:Fill hasColor="false" transparent="false"/>
                    <y:BorderStyle color="#808080" type="line" width="1.0"/>
                    <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="4.0" modelName="internal" modelPosition="c" textColor="#808080" visible="true" width="4.0" x="13.0" y="13.0"><xsl:value-of select="name"/></y:NodeLabel>
                    <y:Shape type="rectangle"/>
                </y:ShapeNode>
            </data>
        </node>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="package">
        <node id="{name}">
            <data key="d0">
                <y:ShapeNode>
                    <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="4.0" modelName="internal" modelPosition="c" textColor="#000000" visible="true" width="4.0" x="13.0" y="13.0"><xsl:value-of select="name"/></y:NodeLabel>
                    <y:Shape type="rectangle"/>
                </y:ShapeNode>
            </data>
        </node>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="class[@confirmed='no'] | feature[@confirmed='no']">
        <node id="{name}">
            <data key="d0">
                <y:ShapeNode>
                    <y:Fill hasColor="false" transparent="false"/>
                    <y:BorderStyle color="#808080" type="line" width="1.0"/>
                    <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="4.0" modelName="internal" modelPosition="c" textColor="#808080" visible="true" width="4.0" x="13.0" y="13.0"><xsl:value-of select="name"/></y:NodeLabel>
                    <y:Shape type="rectangle"/>
                </y:ShapeNode>
            </data>
        </node>
        <edge source="{../name}" target="{name}" directed="false">
            <data key="d1">
                <y:PolyLineEdge>
                    <y:LineStyle color="#000000" type="line" width="1.0"/>
                </y:PolyLineEdge>
            </data>
        </edge>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="class | feature">
        <node id="{name}">
            <data key="d0">
                <y:ShapeNode>
                    <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="4.0" modelName="internal" modelPosition="c" textColor="#000000" visible="true" width="4.0" x="13.0" y="13.0"><xsl:value-of select="name"/></y:NodeLabel>
                    <y:Shape type="rectangle"/>
                </y:ShapeNode>
            </data>
        </node>
        <edge source="{../name}" target="{name}" directed="false">
            <data key="d1">
                <y:PolyLineEdge>
                    <y:LineStyle color="#000000" type="line" width="1.0"/>
                </y:PolyLineEdge>
            </data>
        </edge>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="inbound">
        <edge source="{.}" target="{../name}">
            <data key="d1">
                <y:PolyLineEdge>
                    <y:LineStyle color="#000000" type="dashed" width="1.0"/>
                    <y:Arrows source="none" target="standard"/>
                </y:PolyLineEdge>
            </data>
        </edge>
    </xsl:template>

    <xsl:template match="outbound">
        <edge source="{../name}" target="{.}">
            <data key="d1">
                <y:PolyLineEdge>
                    <y:LineStyle color="#000000" type="dashed" width="1.0"/>
                    <y:Arrows source="none" target="standard"/>
                </y:PolyLineEdge>
            </data>
        </edge>
    </xsl:template>

    <xsl:template match="name"/>

</xsl:stylesheet>
