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

package com.jeantessier.dependencyfinder.web;

import com.meterware.httpunit.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TestCyclesBase extends TestBase {
    @Test
    void testDirectQuery() throws Exception {
        request.setParameter("scope-includes", "//");
        request.setParameter("feature-scope", "on");
        request.setParameter("maximum-cycle-length", "");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNull(response.getLinkWith(fooFeatureName), "Unwanted link to " + fooFeatureName);
        assertNull(response.getLinkWith(barFeatureName), "Unwanted link to " + barFeatureName);
        assertNull(response.getLinkWith(bazFeatureName), "Unwanted link to " + bazFeatureName);
        assertNotNull(response.getLinkWith(leftFeatureName), "Missing link to " + leftFeatureName);
        assertNotNull(response.getLinkWith(rightFeatureName), "Missing link to " + rightFeatureName);

        assertNull(response.getLinkWithID(fooFeatureName), "Unwanted link foo");
        assertNull(response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName), "Unwanted link foo --> bar");
        assertNull(response.getLinkWithID(barFeatureName), "Unwanted link bar");
        assertNull(response.getLinkWithID(barFeatureName + "_from_" + fooFeatureName), "Unwanted link bar <-- foo");
        assertNull(response.getLinkWithID(barFeatureName + "_to_" + bazFeatureName), "Unwanted link bar --> baz");
        assertNull(response.getLinkWithID(bazFeatureName), "Unwanted link baz");
        assertNull(response.getLinkWithID(bazFeatureName + "_from_" + barFeatureName), "Unwanted link baz <-- bar");
        assertNotNull(response.getLinkWithID(leftFeatureName), "Missing link left");
        assertNotNull(response.getLinkWithID(leftFeatureName + "_to_" + rightFeatureName), "Missing link left --> right");
        assertNull(response.getLinkWithID(rightFeatureName), "Unwanted link right");
        assertNotNull(response.getLinkWithID(rightFeatureName + "_to_" + leftFeatureName), "Missing link right --> left");
    }
}
