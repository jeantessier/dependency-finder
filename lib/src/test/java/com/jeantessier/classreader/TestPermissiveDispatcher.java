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

package com.jeantessier.classreader;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestPermissiveDispatcher {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("foo.class", ClassfileLoaderAction.CLASS),

                arguments("src", ClassfileLoaderAction.DIRECTORY),

                arguments("MANIFEST.MF", ClassfileLoaderAction.IGNORE),
                arguments("foo.bat", ClassfileLoaderAction.IGNORE),
                arguments("foo.css", ClassfileLoaderAction.IGNORE),
                arguments("foo.dtd", ClassfileLoaderAction.IGNORE),
                arguments("foo.gif", ClassfileLoaderAction.IGNORE),
                arguments("foo.htm", ClassfileLoaderAction.IGNORE),
                arguments("foo.html", ClassfileLoaderAction.IGNORE),
                arguments("foo.java", ClassfileLoaderAction.IGNORE),
                arguments("foo.jpeg", ClassfileLoaderAction.IGNORE),
                arguments("foo.jpg", ClassfileLoaderAction.IGNORE),
                arguments("foo.js", ClassfileLoaderAction.IGNORE),
                arguments("foo.jsp", ClassfileLoaderAction.IGNORE),
                arguments("foo.properties", ClassfileLoaderAction.IGNORE),
                arguments("foo.ps", ClassfileLoaderAction.IGNORE),
                arguments("foo.txt", ClassfileLoaderAction.IGNORE),
                arguments("foo.xml", ClassfileLoaderAction.IGNORE),
                arguments("foo.xsl", ClassfileLoaderAction.IGNORE),
                arguments("foo/", ClassfileLoaderAction.IGNORE),

                arguments("foo.jar", ClassfileLoaderAction.JAR),

                arguments("foo.zip", ClassfileLoaderAction.ZIP),

                arguments("foo.foo", ClassfileLoaderAction.ZIP)
        );
    }

    private final ClassfileLoaderDispatcher dispatcher = new PermissiveDispatcher();

    @DisplayName("ClassfileLoaderDispatcher")
    @ParameterizedTest(name="file \"{0}\" should be {1}")
    @MethodSource("dataProvider")
    void testDispatch(String filename, ClassfileLoaderAction expectedAction) {
        assertEquals(expectedAction, dispatcher.dispatch(filename));
    }
}
