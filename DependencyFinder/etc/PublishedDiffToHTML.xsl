<?xml version="1.0"?> 

<!--
    Copyright (c) 2001-2005, Jean Tessier
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
    xmlns:validator="xalan://com.jeantessier.diff.Validator"
    xmlns:listvalidator="xalan://com.jeantessier.diff.ListBasedValidator"
    exclude-result-prefixes="validator">

    <xsl:output method="html" indent="yes"/>
    <xsl:strip-space elements="*"/> 

    <xsl:param name="validation-list">public_packages.txt</xsl:param>
    <xsl:variable name="validator" select="listvalidator:new($validation-list)"/>

    <xsl:template match="differences">
        <xsl:variable name="new-label" select="new"/>

        <html>

        <head>
            <title><xsl:if test="name/text()"><xsl:value-of select="name"/> - </xsl:if>Public API Change History</title>
        </head>

        <body bgcolor="#ffffff">

        <h1><xsl:if test="name/text()"><xsl:value-of select="name"/> - </xsl:if>Public API Change History</h1>

        <ul>
        <li><a href="#{$new-label}"><xsl:value-of select="old"/> to <xsl:value-of select="new"/></a></li>
        </ul>

        <hr />

        <a name="{$new-label}" />
        <h2><xsl:value-of select="old"/> to <xsl:value-of select="new"/></h2>

        <xsl:apply-templates/>

        <hr />

        </body>

        </html>
    </xsl:template>

    <xsl:template match="differences/name | old | new"></xsl:template>

    <xsl:template match="removed-packages[name[validator:isPackageAllowed($validator,text())]]">
        <h3>Removed Packages:</h3>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="removed-packages"></xsl:template>
 
    <xsl:template match="removed-interfaces[name[@visibility='public' and validator:isClassAllowed($validator,text())]]">
        <h3>Removed Interfaces:</h3>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="removed-interfaces"></xsl:template>
 
    <xsl:template match="removed-classes[name[@visibility='public' and validator:isClassAllowed($validator,text())]]">
        <h3>Removed Classes:</h3>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="removed-classes"></xsl:template>

    <xsl:template match="undocumented-packages"></xsl:template>
 
    <xsl:template match="deprecated-interfaces[name[@visibility='public' and validator:isClassAllowed($validator,text())]]">
        <h3>Newly Deprecated Interfaces:</h3>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="deprecated-interfaces"></xsl:template>
 
    <xsl:template match="deprecated-classes[name[@visibility='public' and validator:isClassAllowed($validator,text())]]">
        <h3>Newly Deprecated Classes:</h3>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="deprecated-classes"></xsl:template>

    <xsl:template match="undocumented-interfaces"></xsl:template>
    <xsl:template match="undocumented-classes"></xsl:template>

    <xsl:template match="modified-interfaces">
        <h3>Modified Interfaces:</h3>
        <blockquote>
            <xsl:apply-templates/>
        </blockquote>
   </xsl:template>
 
    <xsl:template match="modified-classes">
        <h3>Modified Classes:</h3>
        <blockquote>
            <xsl:apply-templates/>
        </blockquote>
    </xsl:template>

    <xsl:template match="documented-interfaces"></xsl:template>
    <xsl:template match="documented-classes"></xsl:template>
    <xsl:template match="undeprecated-interfaces"></xsl:template>
    <xsl:template match="undeprecated-classes"></xsl:template>
 
    <xsl:template match="new-packages"></xsl:template>
    <xsl:template match="documented-packages"></xsl:template>
    <xsl:template match="new-interfaces"></xsl:template>
    <xsl:template match="new-classes"></xsl:template>
 
    <xsl:template match="class[validator:isClassAllowed($validator,name) and
                               (modified-declaration[(old-declaration/@visibility='public' or new-declaration/@visibility='public') and
                                                     ((old-declaration/@visibility='public' and new-declaration/@visibility='package') or
                                                      (not(old-declaration/@abstract) and new-declaration/@abstract) or
                                                      (not(old-declaration/@final) and new-declaration/@final) or
                                                      (old-declaration/@extends!=new-declaration/@extends) or
                                                      (old-declaration/@implements!=new-declaration/@implements))] or
                                removed-fields/declaration[(@visibility='public' or @visibility='protected') and
                                                           validator:isFeatureAllowed($validator,@full-signature)] or
                                removed-constructors/declaration[(@visibility='public' or @visibility='protected') and
                                                                 validator:isFeatureAllowed($validator,@full-signature)] or
                                removed-methods/declaration[(@visibility='public' or @visibility='protected') and
                                                            validator:isFeatureAllowed($validator,@full-signature)] or
                                deprecated-fields/declaration[(@visibility='public' or @visibility='protected') and
                                                              validator:isFeatureAllowed($validator,@full-signature)] or
                                deprecated-constructors/declaration[(@visibility='public' or @visibility='protected') and
                                                                    validator:isFeatureAllowed($validator,@full-signature)] or
                                deprecated-methods/declaration[(@visibility='public' or @visibility='protected') and
                                                               validator:isFeatureAllowed($validator,@full-signature)] or
                                .//feature[validator:isFeatureAllowed($validator,name) and
                                           modified-declaration[(old-declaration/@visibility='public' or
                                                                 old-declaration/@visibility='protected' or
                                                                 new-declaration/@visibility='public' or
                                                                 new-declaration/@visibility='protected') and
                                                                ((old-declaration/@visibility='public' and
                                                                     (new-declaration/@visibility='private' or
                                                                      new-declaration/@visibility='package' or
                                                                      new-declaration/@visibility='protected')) or
                                                                 (old-declaration/@visibility='protected' and
                                                                     (new-declaration/@visibility='package' or
                                                                      new-declaration/@visibility='private')) or
                                                                 (not(old-declaration/@abstract) and new-declaration/@abstract) or
                                                                 (not(old-declaration/@static) and new-declaration/@static) or
                                                                 (not(old-declaration/@final) and new-declaration/@final) or
                                                                 (old-declaration/@type!=new-declaration/@type) or
                                                                 (old-declaration/@return-type!=new-declaration/@return-type) or
                                                                 (old-declaration/@throws!=new-declaration/@throws))]])]">
        <h4><code><xsl:value-of select="name"/></code></h4>
        <blockquote>
            <xsl:if test="modified-declaration[(old-declaration/@visibility='public' or new-declaration/@visibility='public') and
                                               ((old-declaration/@visibility='public' and new-declaration/@visibility='package') or
                                                (not(old-declaration/@abstract) and new-declaration/@abstract) or
                                                (not(old-declaration/@final) and new-declaration/@final) or
                                                (old-declaration/@extends!=new-declaration/@extends) or
                                                (old-declaration/@implements!=new-declaration/@implements))]">
                <h5>Declaration Changes:</h5>
            </xsl:if>
            <xsl:apply-templates/>
        </blockquote>
    </xsl:template>

    <xsl:template match="class"></xsl:template>

    <xsl:template match="removed-fields[declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature) and not(@inherited)]]">
        <h5>Removed Fields:</h5>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
 
    <xsl:template match="removed-constructors[declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature) and not(@inherited)]]">
        <h5>Removed Constructors:</h5>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
 
    <xsl:template match="removed-methods[declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature) and not(@inherited)]]">
        <h5>Removed Methods:</h5>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
 
    <xsl:template match="deprecated-fields[declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature)]]">
        <h5>Newly Deprecated Fields:</h5>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
 
    <xsl:template match="deprecated-constructors[declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature)]]">
        <h5>Newly Deprecated Constructors:</h5>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
 
    <xsl:template match="deprecated-methods[declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature)]]">
        <h5>Newly Deprecated Methods:</h5>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>

    <xsl:template match="undocumented-fields"></xsl:template>
    <xsl:template match="undocumented-constructors"></xsl:template>
    <xsl:template match="undocumented-methods"></xsl:template>

    <xsl:template match="modified-fields[feature[validator:isFeatureAllowed($validator,name) and
                                                 modified-declaration[(old-declaration/@visibility='public' or
                                                                       old-declaration/@visibility='protected' or
                                                                       new-declaration/@visibility='public' or
                                                                       new-declaration/@visibility='protected') and
                                                                      ((old-declaration/@visibility='public' and
                                                                           (new-declaration/@visibility='private' or
                                                                            new-declaration/@visibility='package' or
                                                                            new-declaration/@visibility='protected')) or
                                                                       (old-declaration/@visibility='protected' and
                                                                           (new-declaration/@visibility='package' or
                                                                            new-declaration/@visibility='private')) or
                                                                       (not(old-declaration/@static) and new-declaration/@static) or
                                                                       (not(old-declaration/@final) and new-declaration/@final) or
                                                                       (old-declaration/@type!=new-declaration/@type))]]]">
        <h5>Field Declaration Changes:</h5>
        <xsl:apply-templates/>
    </xsl:template>
 
    <xsl:template match="modified-constructors[feature[validator:isFeatureAllowed($validator,name) and
                                                       modified-declaration[(old-declaration/@visibility='public' or
                                                                             old-declaration/@visibility='protected' or
                                                                             new-declaration/@visibility='public' or
                                                                             new-declaration/@visibility='protected') and
                                                                            ((old-declaration/@visibility='public' and
                                                                                 (new-declaration/@visibility='private' or
                                                                                  new-declaration/@visibility='package' or
                                                                                  new-declaration/@visibility='protected')) or
                                                                             (old-declaration/@visibility='protected' and
                                                                                 (new-declaration/@visibility='package' or
                                                                                  new-declaration/@visibility='private')) or
                                                                             (not(old-declaration/@abstract) and new-declaration/@abstract) or
                                                                             (not(old-declaration/@static) and new-declaration/@static) or
                                                                             (not(old-declaration/@final) and new-declaration/@final) or
                                                                             (old-declaration/@return-type!=new-declaration/@return-type) or
                                                                             (old-declaration/@throws!=new-declaration/@throws))]]]">
        <h5>Constructor Declaration Changes:</h5>
        <xsl:apply-templates/>
    </xsl:template>
 
    <xsl:template match="modified-methods[feature[validator:isFeatureAllowed($validator,name) and
                                                  modified-declaration[(old-declaration/@visibility='public' or
                                                                        old-declaration/@visibility='protected' or
                                                                        new-declaration/@visibility='public' or
                                                                        new-declaration/@visibility='protected') and
                                                                       ((old-declaration/@visibility='public' and
                                                                            (new-declaration/@visibility='private' or
                                                                             new-declaration/@visibility='package' or
                                                                             new-declaration/@visibility='protected')) or
                                                                        (old-declaration/@visibility='protected' and
                                                                            (new-declaration/@visibility='package' or
                                                                             new-declaration/@visibility='private')) or
                                                                        (not(old-declaration/@abstract) and new-declaration/@abstract) or
                                                                        (not(old-declaration/@static) and new-declaration/@static) or
                                                                        (not(old-declaration/@final) and new-declaration/@final) or
                                                                        (old-declaration/@return-type!=new-declaration/@return-type) or
                                                                        (old-declaration/@throws!=new-declaration/@throws))]]]">
        <h5>Method Declaration Changes:</h5>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="documented-fields"></xsl:template>
    <xsl:template match="documented-constructors"></xsl:template>
    <xsl:template match="documented-methods"></xsl:template>
 
    <xsl:template match="undeprecated-fields"></xsl:template>
    <xsl:template match="undeprecated-constructors"></xsl:template>
    <xsl:template match="undeprecated-methods"></xsl:template>
 
    <xsl:template match="new-fields"></xsl:template>
    <xsl:template match="new-constructors"></xsl:template>
    <xsl:template match="new-methods"></xsl:template>
 
    <xsl:template match="feature[validator:isFeatureAllowed($validator,name)]">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="feature"></xsl:template>

    <xsl:template match="modified-declaration[(old-declaration/@visibility='public' or
                                               old-declaration/@visibility='protected' or
                                               new-declaration/@visibility='public' or
                                               new-declaration/@visibility='protected') and
                                              ((old-declaration/@visibility='public' and
                                                   (new-declaration/@visibility='private' or
                                                    new-declaration/@visibility='package' or
                                                    new-declaration/@visibility='protected')) or
                                               (old-declaration/@visibility='protected' and
                                                   (new-declaration/@visibility='package' or
                                                    new-declaration/@visibility='private')) or
                                               (not(old-declaration/@abstract) and new-declaration/@abstract) or
                                               (not(old-declaration/@static) and new-declaration/@static) or
                                               (not(old-declaration/@final) and new-declaration/@final) or
                                               (old-declaration/@extends!=new-declaration/@extends) or
                                               (old-declaration/@implements!=new-declaration/@implements) or
                                               (old-declaration/@type!=new-declaration/@type) or
                                               (old-declaration/@return-type!=new-declaration/@return-type) or
                                               (old-declaration/@throws!=new-declaration/@throws))]">
        <blockquote>
        <p><nobr><code>
        <b>old:</b> <xsl:value-of select="old-declaration"/>
        <xsl:if test="old-declaration[@deprecated='yes']"> <b>[deprecated]</b></xsl:if>
        <br/>
        <b>new:</b> <xsl:value-of select="new-declaration"/>
        <xsl:if test="self::node()[@deprecated='yes']"> <b>[deprecated]</b></xsl:if>
        </code></nobr></p>
        </blockquote>
    </xsl:template>
    <xsl:template match="modified-declaration"></xsl:template>

    <xsl:template match="removed-packages/name[validator:isPackageAllowed($validator,text())] |
                         name[@visibility='public' and validator:isClassAllowed($validator,text())]">
        <li><nobr><code><xsl:value-of select="."/></code></nobr></li>
    </xsl:template>
    <xsl:template match="name"></xsl:template>

    <xsl:template match="declaration[(@visibility='public' or @visibility='protected') and validator:isFeatureAllowed($validator,@full-signature)]">
        <li><nobr><code><xsl:value-of select="."/></code></nobr></li>
    </xsl:template>
    <xsl:template match="declaration"></xsl:template>

</xsl:stylesheet>

