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

package com.jeantessier.dependencyfinder.web;

import com.meterware.httpunit.*;

public abstract class TestCyclesBase extends TestBase {
    public void testDirectQuery() throws Exception {
        request.setParameter("scope-includes", "//");
        request.setParameter("feature-scope", "on");
        request.setParameter("maximum-cycle-length", "");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNull("Unwanted link to " + fooFeatureName, response.getLinkWith(fooFeatureName));
        assertNull("Unwanted link to " + barFeatureName, response.getLinkWith(barFeatureName));
        assertNull("Unwanted link to " + bazFeatureName, response.getLinkWith(bazFeatureName));
        assertNotNull("Missing link to " + leftFeatureName, response.getLinkWith(leftFeatureName));
        assertNotNull("Missing link to " + rightFeatureName, response.getLinkWith(rightFeatureName));

        assertNull("Unwanted link foo", response.getLinkWithID(fooFeatureName));
        assertNull("Unwanted link foo --> bar", response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName));
        assertNull("Unwanted link bar", response.getLinkWithID(barFeatureName));
        assertNull("Unwanted link bar <-- foo", response.getLinkWithID(barFeatureName + "_from_" + fooFeatureName));
        assertNull("Unwanted link bar --> baz", response.getLinkWithID(barFeatureName + "_to_" + bazFeatureName));
        assertNull("Unwanted link baz", response.getLinkWithID(bazFeatureName));
        assertNull("Unwanted link baz <-- bar", response.getLinkWithID(bazFeatureName + "_from_" + barFeatureName));
        assertNotNull("Missing link left", response.getLinkWithID(leftFeatureName));
        assertNotNull("Missing link left --> right", response.getLinkWithID(leftFeatureName + "_to_" + rightFeatureName));
        assertNull("Unwanted link right", response.getLinkWithID(rightFeatureName));
        assertNotNull("Missing link right --> left", response.getLinkWithID(rightFeatureName + "_to_" + leftFeatureName));
    }
}
