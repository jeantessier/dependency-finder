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

import com.jeantessier.classreader.*;

public class TestDifferencesFactoryWithStrategy extends TestDifferencesFactoryBase {
    private MockDifferenceStrategy strategy;
    private DifferencesFactory     factory;

    protected void setUp() throws Exception {
        super.setUp();

        strategy = new MockDifferenceStrategy(new APIDifferenceStrategy(new CodeDifferenceStrategy()));
        factory  = new DifferencesFactory(strategy);
    }

    public void testEmptyProject() throws IOException {
        factory.createProjectDifferences("test", "old", new PackageMapper(), "new", new PackageMapper());

        assertEquals("package count",  0, strategy.getPackageDifferentCount());
        assertEquals("class count",    0, strategy.getClassDifferentCount());
        assertEquals("field count",    0, strategy.getFieldDifferentCount());
        assertEquals("constant count", 0, strategy.getConstantValueDifferentCount());
        assertEquals("method count",   0, strategy.getMethodDifferentCount());
        assertEquals("code count",     0, strategy.getCodeDifferentCount());
    }

    public void testStrategy() throws IOException {
        factory.createProjectDifferences("test", "old", getOldPackages(), "new", getNewPackages());

        assertEquals("package count",   4, strategy.getPackageDifferentCount());
        assertEquals("class count",    13, strategy.getClassDifferentCount());
        assertEquals("field count",    33, strategy.getFieldDifferentCount());
        assertEquals("constant count", 13, strategy.getConstantValueDifferentCount());
        assertEquals("method count",   31, strategy.getMethodDifferentCount());
        assertEquals("code count",     11, strategy.getCodeDifferentCount());
    }
}
