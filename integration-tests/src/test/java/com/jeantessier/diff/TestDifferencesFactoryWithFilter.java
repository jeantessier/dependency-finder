/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.jmock.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDifferencesFactoryWithFilter extends TestDifferencesFactoryBase {
    private final StringBuffer buffer = new StringBuffer();
    private final DifferenceStrategy mockStrategy = mock(DifferenceStrategy.class);
    private ListBasedDifferenceStrategy strategy;
    private DifferencesFactory factory;

    @BeforeEach
    void setUp() throws Exception {
        strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        factory = new DifferencesFactory(strategy);
    }

    @Test
    void testEmptyFilter() throws IOException {
        ProjectDifferences differences = getDifferences();

        assertTrue(differences.getPackageDifferences().isEmpty());
    }

    @Test
    void testFilter() throws IOException {
        buffer.append("ModifiedPackage\n");
        buffer.append("ModifiedPackage.ModifiedClass\n");
        buffer.append("ModifiedPackage.ModifiedClass.modifiedField\n");
        buffer.append("ModifiedPackage.ModifiedClass.modifiedMethod()\n");

        checking(new Expectations() {{
            oneOf (mockStrategy).isPackageDifferent(findPackage("ModifiedPackage", getOldPackages()), findPackage("ModifiedPackage", getNewPackages()));
                will(returnValue(true));
            oneOf (mockStrategy).isClassDifferent(findClass("ModifiedPackage.ModifiedClass", getOldPackages()), findClass("ModifiedPackage.ModifiedClass", getNewPackages()));
                will(returnValue(true));
            oneOf (mockStrategy).isDeclarationModified(findClass("ModifiedPackage.ModifiedClass", getOldPackages()), findClass("ModifiedPackage.ModifiedClass", getNewPackages()));
            oneOf (mockStrategy).isFieldDifferent(findField("ModifiedPackage.ModifiedClass.modifiedField", getOldPackages()), findField("ModifiedPackage.ModifiedClass.modifiedField", getNewPackages()));
                will(returnValue(true));
            oneOf (mockStrategy).isConstantValueDifferent(findField("ModifiedPackage.ModifiedClass.modifiedField", getOldPackages()).getConstantValue(), findField("ModifiedPackage.ModifiedClass.modifiedField", getNewPackages()).getConstantValue());
            oneOf (mockStrategy).isMethodDifferent(findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getOldPackages()), findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getNewPackages()));
                will(returnValue(true));
            oneOf (mockStrategy).isCodeDifferent(findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getOldPackages()).getCode(), findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getNewPackages()).getCode());
        }});

        ProjectDifferences differences = getDifferences();
        assertEquals(1, differences.getPackageDifferences().size(), "Nb packages");

        PackageDifferences modifiedPackage = (PackageDifferences) find("ModifiedPackage", differences.getPackageDifferences());
        assertEquals(1, modifiedPackage.getClassDifferences().size(), "Nb classes");

        ClassDifferences modifiedClass = (ClassDifferences) find("ModifiedPackage.ModifiedClass", modifiedPackage.getClassDifferences());
        assertEquals(2, modifiedClass.getFeatureDifferences().size(), "Nb features");
    }

    private ProjectDifferences getDifferences() throws IOException {
        strategy.load(new BufferedReader(new StringReader(buffer.toString())));
        return (ProjectDifferences) factory.createProjectDifferences("test", "old", getOldPackages(), "new", getNewPackages());
    }
}
