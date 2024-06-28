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

import java.util.*;

import com.jeantessier.classreader.*;
import org.jmock.*;

public class TestDifferencesFactoryWithStrategy extends TestDifferencesFactoryBase {
    private DifferenceStrategy mockStrategy;
    private DifferencesFactory factory;

    protected void setUp() throws Exception {
        super.setUp();

        mockStrategy = mock(DifferenceStrategy.class);
        factory  = new DifferencesFactory(mockStrategy);
    }

    public void testEmptyProject()  {
        factory.createProjectDifferences("test", "old", new PackageMapper(), "new", new PackageMapper());
    }

    public void testStrategy() {
        checking(new Expectations() {{
            exactly(4).of (mockStrategy).isPackageDifferent(with(any(Map.class)), with(any(Map.class)));
                will(returnValue(true));

            exactly(11).of (mockStrategy).isClassDifferent(with(any(Classfile.class)), with(any(Classfile.class)));
                will(returnValue(true));
            exactly(2).of (mockStrategy).isClassDifferent(with(any(Classfile.class)), with(aNull(Classfile.class)));
                will(returnValue(true));
            exactly(2).of (mockStrategy).isClassDifferent(with(aNull(Classfile.class)), with(any(Classfile.class)));
                will(returnValue(true));

            exactly(11).of (mockStrategy).isDeclarationModified(with(any(Classfile.class)), with(any(Classfile.class)));
                will(returnValue(true));

            exactly(19).of (mockStrategy).isFieldDifferent(with(any(Field_info.class)), with(any(Field_info.class)));
                will(returnValue(true));
            exactly(8).of (mockStrategy).isFieldDifferent(with(aNull(Field_info.class)), with(any(Field_info.class)));
                will(returnValue(true));
            exactly(8).of (mockStrategy).isFieldDifferent(with(any(Field_info.class)), with(aNull(Field_info.class)));
                will(returnValue(true));

            exactly(13).of (mockStrategy).isConstantValueDifferent(with(any(ConstantValue_attribute.class)), with(any(ConstantValue_attribute.class)));
                will(returnValue(true));
            exactly(6).of (mockStrategy).isConstantValueDifferent(with(aNull(ConstantValue_attribute.class)), with(aNull(ConstantValue_attribute.class)));
                will(returnValue(true));

            exactly(26).of (mockStrategy).isMethodDifferent(with(any(Method_info.class)), with(any(Method_info.class)));
                will(returnValue(true));
            exactly(5).of (mockStrategy).isMethodDifferent(with(aNull(Method_info.class)), with(any(Method_info.class)));
                will(returnValue(true));
            exactly(3).of (mockStrategy).isMethodDifferent(with(any(Method_info.class)), with(aNull(Method_info.class)));
                will(returnValue(true));

            exactly(20).of (mockStrategy).isCodeDifferent(with(any(Code_attribute.class)), with(any(Code_attribute.class)));
                will(returnValue(true));
            exactly(6).of (mockStrategy).isCodeDifferent(with(aNull(Code_attribute.class)), with(aNull(Code_attribute.class)));
                will(returnValue(true));
        }});

        factory.createProjectDifferences("test", "old", getOldPackages(), "new", getNewPackages());
    }
}
