/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestAggregatingClassfileLoader.class,
        TestAggregatingClassfileLoaderWithModifiedOnlyDispatcher.class,
        TestAnnotation.class,
        TestBitFormat.class,
        TestClassNameHelper.class,
        TestClassfile.class,
        TestClassfileFilteringLoadListener.class,
        TestClassfileLoaderPermissiveDispatcher.class,
        TestClassfileLoaderStrictDispatcher.class,
        TestClassfileScanner.class,
        TestCodeFinder.class,
        TestDeprecationDetector.class,
        TestDeprecationPrinter.class,
        TestDescriptorHelper.class,
        TestDirectoryClassfileLoader.class,
        TestDirectoryExplorer.class,
        TestEnclosingMethodAttribute.class,
        TestEnum.class,
        TestFileFilteringLoadListener.class,
        TestFilteringSymbolGathererStrategy.class,
        TestFinalMethodOrClassSymbolGathererStrategy.class,
        TestGroupFilteringLoadListener.class,
        TestInstructionWithDifferentConstantPool.class,
        TestJarClassfileLoader.class,
        TestLoadListenerDecorator.class,
        TestLoadListenerVisitorAdapter.class,
        TestLocalVariableFinder.class,
        TestLocalVariableTypeTableAttribute.class,
        TestModifiedOnlyDispatcher.class,
        TestMonitor.class,
        TestNonPrivateFieldSymbolGathererStrategy.class,
        TestPackageMapper.class,
        TestPermissiveDispatcher.class,
        TestSignatureAttribute.class,
        TestSignatureFinder.class,
        TestSignatureHelper.class,
        TestStrictDispatcher.class,
        TestSymbolGatherer.class,
        TestSymbolGathererStrategyDecorator.class,
        TestSymbolGathererWithStrategy.class,
        TestTextPrinter.class,
        TestTransientClassfileLoader.class,
        TestVarargs.class,
        TestVerificationType.class,
        TestVerificationType_Invalid.class,
        TestVisitorBase.class,
        TestXMLPrinter.class,
        TestXMLPrinterEscaping.class,
        TestZipClassfileLoader.class,
})
public class TestAll {
}
