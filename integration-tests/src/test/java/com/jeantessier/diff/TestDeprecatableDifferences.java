/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import com.jeantessier.classreader.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestDeprecatableDifferences extends TestDifferencesFactoryBase {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("class that is never deprecated", "ModifiedPackage.ModifiedClass", false, false),
                arguments("class gets undeprecated by annotation", "ModifiedPackage.UndeprecatedClassByAnnotation", false, true),
                arguments("class gets undeprecated by Javadoc tag", "ModifiedPackage.UndeprecatedClassByJavadocTag", false, true),
                arguments("class gets deprecated by annotation", "ModifiedPackage.DeprecatedClassByAnnotation", true, false),
                arguments("class gets deprecated by Javadoc tag", "ModifiedPackage.DeprecatedClassByJavadocTag", true, false),

                arguments("interface that is never deprecated", "ModifiedPackage.ModifiedInterface", false, false),
                arguments("interface gets undeprecated by annotation", "ModifiedPackage.UndeprecatedInterfaceByAnnotation", false, true),
                arguments("interface gets undeprecated by Javadoc tag", "ModifiedPackage.UndeprecatedInterfaceByJavadocTag", false, true),
                arguments("interface gets deprecated by annotation", "ModifiedPackage.DeprecatedInterfaceByAnnotation", true, false),
                arguments("interface gets deprecated by Javadoc tag", "ModifiedPackage.DeprecatedInterfaceByJavadocTag", true, false)
        );
    }

    private final DifferencesFactory factory = new DifferencesFactory();

    @DisplayName("deprecatable differences")
    @ParameterizedTest(name = "deprecation for {0} with class {1} should be new ({2}) or removed ({3})")
    @MethodSource("dataProvider")
    void compareClasses(String variation, String className, boolean isNew, boolean isRemoved) {
        Classfile oldClassfile = oldLoader.getClassfile(className);
        assertNotNull(oldClassfile);
        Classfile newClassfile = newLoader.getClassfile(className);
        assertNotNull(newClassfile);
        Differences componentDifferences = factory.createClassDifferences(className, oldClassfile, newClassfile);

        DeprecatableDifferences deprecatedDifferences = new DeprecatableDifferences(componentDifferences, oldClassfile, newClassfile);
        assertEquals(isNew, deprecatedDifferences.isNewDeprecation(), "deprecated NewDeprecation()");
        assertEquals(isRemoved, deprecatedDifferences.isRemovedDeprecation(), "deprecated RemovedDeprecation()");
    }
}
