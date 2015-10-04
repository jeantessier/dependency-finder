/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.diff;

import java.io.*;

public class TestDifferencesFactoryWithFilter extends TestDifferencesFactoryBase {
    private StringBuffer                buffer;
    private MockDifferenceStrategy      mockStrategy;
    private ListBasedDifferenceStrategy strategy;
    private DifferencesFactory          factory;

    protected void setUp() throws Exception {
        super.setUp();

        buffer   = new StringBuffer();
        mockStrategy = new MockDifferenceStrategy(new APIDifferenceStrategy(new NoDifferenceStrategy()));
        strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        factory  = new DifferencesFactory(strategy);
    }

    public void testEmptyFilter() throws IOException {
        ProjectDifferences differences = getDifferences();

        assertTrue("IsEmpty()", differences.getPackageDifferences().isEmpty());

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFilter() throws IOException {
        buffer.append("ModifiedPackage\n");
        buffer.append("ModifiedPackage.ModifiedClass\n");
        buffer.append("ModifiedPackage.ModifiedClass.modifiedField\n");
        buffer.append("ModifiedPackage.ModifiedClass.modifiedMethod()\n");

        ProjectDifferences differences = getDifferences();
        assertEquals("Nb packages", 1, differences.getPackageDifferences().size());

        PackageDifferences modifiedPackage = (PackageDifferences) find("ModifiedPackage", differences.getPackageDifferences());
        assertEquals("Nb classes",  1, modifiedPackage.getClassDifferences().size());

        ClassDifferences modifedClass = (ClassDifferences) find("ModifiedPackage.ModifiedClass", modifiedPackage.getClassDifferences());
        assertEquals("Nb features",  2, modifedClass.getFeatureDifferences().size());

        assertEquals("package",  1, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    1, mockStrategy.getClassDifferentCount());
        assertEquals("field",    1, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   1, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    private ProjectDifferences getDifferences() throws IOException {
        strategy.load(new BufferedReader(new StringReader(buffer.toString())));
        return (ProjectDifferences) factory.createProjectDifferences("test", "old", getOldPackages(), "new", getNewPackages());
    }
}
