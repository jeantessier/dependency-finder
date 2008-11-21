/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

package com.jeantessier.classreader;

import junit.framework.*;

public class TestAll extends TestCase {
    public static Test suite() {
        TestSuite result = new TestSuite(TestAll.class.getPackage().getName());

        result.addTestSuite(TestBitFormat.class);
        result.addTestSuite(TestInstructionWithDifferentConstantPool.class);
        result.addTestSuite(TestClassNameHelper.class);
        result.addTestSuite(TestDescriptorHelper.class);
        result.addTestSuite(TestSignatureHelper.class);
        result.addTestSuite(TestVisitorBase.class);
        result.addTestSuite(TestDeprecationDetector.class);
        result.addTestSuite(TestLocalVariableFinder.class);
        result.addTestSuite(TestDirectoryExplorer.class);
        result.addTestSuite(TestAggregatingClassfileLoader.class);
        result.addTestSuite(TestTransientClassfileLoader.class);
        result.addTestSuite(TestDirectoryClassfileLoader.class);
        result.addTestSuite(TestClassfile.class);
        result.addTestSuite(TestPermissiveDispatcher.class);
        result.addTestSuite(TestStrictDispatcher.class);
        result.addTestSuite(TestModifiedOnlyDispatcher.class);
        result.addTestSuite(TestAggregatingClassfileLoaderWithModifiedOnlyDispatcher.class);
        result.addTestSuite(TestZipClassfileLoader.class);
        result.addTestSuite(TestJarClassfileLoader.class);
        result.addTestSuite(TestClassfileLoaderPermissiveDispatcher.class);
        result.addTestSuite(TestClassfileLoaderStrictDispatcher.class);
        result.addTestSuite(TestPackageMapper.class);
        result.addTestSuite(TestClassfileScanner.class);
        result.addTestSuite(TestXMLPrinter.class);
        result.addTestSuite(TestDeprecationPrinter.class);
        result.addTestSuite(TestLoadListenerDecorator.class);
        result.addTestSuite(TestGroupFilteringLoadListener.class);
        result.addTestSuite(TestFileFilteringLoadListener.class);
        result.addTestSuite(TestClassfileFilteringLoadListener.class);
        result.addTestSuite(TestLoadListenerVisitorAdapter.class);
        result.addTestSuite(TestMonitor.class);
        result.addTestSuite(TestSymbolGathererStrategyDecorator.class);
        result.addTestSuite(TestFilteringSymbolGathererStrategy.class);
        result.addTestSuite(TestNonPrivateFieldSymbolGathererStrategy.class);
        result.addTestSuite(TestFinalMethodOrClassSymbolGathererStrategy.class);
        result.addTestSuite(TestSymbolGatherer.class);
        result.addTestSuite(TestSymbolGathererWithStrategy.class);
        result.addTestSuite(TestAnnotation.class);
        result.addTestSuite(TestEnum.class);
        result.addTestSuite(TestVarargs.class);
        result.addTestSuite(TestEnclosingMethodAttribute.class);
        result.addTestSuite(TestSignatureAttribute.class);
        result.addTestSuite(TestLocalVariableTypeTableAttribute.class);

        return result;
    }
}
