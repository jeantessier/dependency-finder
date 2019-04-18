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

public abstract class TestClosureBase extends TestBase {
    public void testDirectQuery() throws Exception {
        request.setParameter("start-includes", "/" + barPackageName + "/");
        request.setParameter("scope", "package");
        request.setParameter("filter", "package");
        request.setParameter("maximum-inbound-depth", "");
        request.setParameter("maximum-outbound-depth", "");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNotNull("Missing link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNull("Unwanted link to " + fooClassName, response.getLinkWith(fooClassName));
        assertNull("Unwanted link to " + fooFeatureName, response.getLinkWith(fooFeatureName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNull("Unwanted link to " + barClassName, response.getLinkWith(barClassName));
        assertNull("Unwanted link to " + barFeatureName, response.getLinkWith(barFeatureName));
        assertNotNull("Missing link to " + bazPackageName, response.getLinkWith(bazPackageName));
        assertNull("Unwanted link to " + bazClassName, response.getLinkWith(bazClassName));
        assertNull("Unwanted link to " + bazFeatureName, response.getLinkWith(bazFeatureName));
        assertNull("Unwanted link to " + leftPackageName, response.getLinkWith(leftPackageName));
        assertNull("Unwanted link to " + leftClassName, response.getLinkWith(leftClassName));
        assertNull("Unwanted link to " + leftFeatureName, response.getLinkWith(leftFeatureName));
        assertNull("Unwanted link to " + rightPackageName, response.getLinkWith(rightPackageName));
        assertNull("Unwanted link to " + rightClassName, response.getLinkWith(rightClassName));
        assertNull("Unwanted link to " + rightFeatureName, response.getLinkWith(rightFeatureName));

        assertNotNull("Missing link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link foo --> bar", response.getLinkWithID(fooPackageName + "_to_" + barPackageName));
        assertNotNull("Missing link bar", response.getLinkWithID(barPackageName));
        assertNotNull("Missing link bar <-- foo", response.getLinkWithID(barPackageName + "_from_" + fooPackageName));
        assertNotNull("Missing link bar --> baz", response.getLinkWithID(barPackageName + "_to_" + bazPackageName));
        assertNotNull("Missing link baz", response.getLinkWithID(bazPackageName));
        assertNotNull("Missing link baz <-- bar", response.getLinkWithID(bazPackageName + "_from_" + barPackageName));
        assertNull("Unwanted link left", response.getLinkWithID(leftPackageName));
        assertNull("Unwanted link left <-> right", response.getLinkWithID(leftPackageName + "_bidirectional_" + rightPackageName));
        assertNull("Unwanted link right", response.getLinkWithID(rightPackageName));
        assertNull("Unwanted link right <-> left", response.getLinkWithID(rightPackageName + "_bidirectional_" + leftPackageName));
    }

    public void testFollowDownstreamLink() throws Exception {
        request.setParameter("start-includes", "/" + fooPackageName + "/");
        request.setParameter("scope", "package");
        request.setParameter("filter", "package");
        request.setParameter("maximum-inbound-depth", "0");
        request.setParameter("maximum-outbound-depth", "1");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNotNull("Missing link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNull("Unwanted link to " + bazPackageName, response.getLinkWith(bazPackageName));

        assertNotNull("Missing link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link foo --> bar", response.getLinkWithID(fooPackageName + "_to_" + barPackageName));
        assertNotNull("Missing link bar", response.getLinkWithID(barPackageName));
        assertNotNull("Missing link bar <-- foo", response.getLinkWithID(barPackageName + "_from_" + fooPackageName));
        assertNull("Unwanted link bar --> baz", response.getLinkWithID(barPackageName + "_to_" + bazPackageName));
        assertNull("Unwanted link baz", response.getLinkWithID(bazPackageName));
        assertNull("Unwanted link baz <-- bar", response.getLinkWithID(bazPackageName + "_from_" + barPackageName));

        response = response.getLinkWithID(fooPackageName + "_to_" + barPackageName).click();

        assertNull("Unwanted link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNotNull("Missing link to " + bazPackageName, response.getLinkWith(bazPackageName));

        assertNull("Unwanted link foo", response.getLinkWithID(fooPackageName));
        assertNull("Unwanted link foo --> bar", response.getLinkWithID(fooPackageName + "_to_" + barPackageName));
        assertNotNull("Missing link bar", response.getLinkWithID(barPackageName));
        assertNull("Unwanted link bar <-- foo", response.getLinkWithID(barPackageName + "_from_" + fooPackageName));
        assertNotNull("Missing link bar --> baz", response.getLinkWithID(barPackageName + "_to_" + bazPackageName));
        assertNotNull("Missing link baz", response.getLinkWithID(bazPackageName));
        assertNotNull("Missing link baz <-- bar", response.getLinkWithID(bazPackageName + "_from_" + barPackageName));
    }
}
