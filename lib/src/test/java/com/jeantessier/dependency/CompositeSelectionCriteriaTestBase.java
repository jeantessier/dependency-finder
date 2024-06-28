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

package com.jeantessier.dependency;

import org.jmock.*;
import org.jmock.imposters.*;
import org.jmock.integration.junit4.*;
import org.junit.*;

import java.util.*;

public class CompositeSelectionCriteriaTestBase {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private static class Counter {
        private int count = 0;

        public int next() {
            return ++count;
        }
    }

    protected Collection<SelectionCriteria> build(Boolean[] subcriteria) {
        var counter = new Counter();
        return Arrays.stream(subcriteria)
                .map(value -> {
                    var selectionCriteria = context.mock(SelectionCriteria.class, "subcriteria " + counter.next());
                    context.checking(new Expectations() {{
                        allowing (selectionCriteria).isMatchingPackages();
                            will(returnValue(value));
                        allowing (selectionCriteria).isMatchingClasses();
                            will(returnValue(value));
                        allowing (selectionCriteria).isMatchingFeatures();
                            will(returnValue(value));
                        allowing (selectionCriteria).matches(with(any(PackageNode.class)));
                            will(returnValue(value));
                        allowing (selectionCriteria).matches(with(any(ClassNode.class)));
                            will(returnValue(value));
                        allowing (selectionCriteria).matches(with(any(FeatureNode.class)));
                            will(returnValue(value));
                        allowing (selectionCriteria).matchesPackageName(with(any(String.class)));
                            will(returnValue(value));
                        allowing (selectionCriteria).matchesClassName(with(any(String.class)));
                            will(returnValue(value));
                        allowing (selectionCriteria).matchesFeatureName(with(any(String.class)));
                            will(returnValue(value));
                    }});
                    return selectionCriteria;
                })
                .toList();
    }
}
