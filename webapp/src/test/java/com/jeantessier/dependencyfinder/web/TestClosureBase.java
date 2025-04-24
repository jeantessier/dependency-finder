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

package com.jeantessier.dependencyfinder.web;

import com.meterware.httpunit.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TestClosureBase extends TestBase {
    @Test
    void testDirectQuery() throws Exception {
        request.setParameter("start-includes", "/" + barPackageName + "/");
        request.setParameter("scope", "package");
        request.setParameter("filter", "package");
        request.setParameter("maximum-inbound-depth", "");
        request.setParameter("maximum-outbound-depth", "");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNotNull(response.getLinkWith(fooPackageName), "Missing link to " + fooPackageName);
        assertNull(response.getLinkWith(fooClassName), "Unwanted link to " + fooClassName);
        assertNull(response.getLinkWith(fooFeatureName), "Unwanted link to " + fooFeatureName);
        assertNotNull(response.getLinkWith(barPackageName), "Missing link to " + barPackageName);
        assertNull(response.getLinkWith(barClassName), "Unwanted link to " + barClassName);
        assertNull(response.getLinkWith(barFeatureName), "Unwanted link to " + barFeatureName);
        assertNotNull(response.getLinkWith(bazPackageName), "Missing link to " + bazPackageName);
        assertNull(response.getLinkWith(bazClassName), "Unwanted link to " + bazClassName);
        assertNull(response.getLinkWith(bazFeatureName), "Unwanted link to " + bazFeatureName);
        assertNull(response.getLinkWith(leftPackageName), "Unwanted link to " + leftPackageName);
        assertNull(response.getLinkWith(leftClassName), "Unwanted link to " + leftClassName);
        assertNull(response.getLinkWith(leftFeatureName), "Unwanted link to " + leftFeatureName);
        assertNull(response.getLinkWith(rightPackageName), "Unwanted link to " + rightPackageName);
        assertNull(response.getLinkWith(rightClassName), "Unwanted link to " + rightClassName);
        assertNull(response.getLinkWith(rightFeatureName), "Unwanted link to " + rightFeatureName);

        assertNotNull(response.getLinkWithID(fooPackageName), "Missing link foo");
        assertNotNull(response.getLinkWithID(fooPackageName + "_to_" + barPackageName), "Missing link foo --> bar");
        assertNotNull(response.getLinkWithID(barPackageName), "Missing link bar");
        assertNotNull(response.getLinkWithID(barPackageName + "_from_" + fooPackageName), "Missing link bar <-- foo");
        assertNotNull(response.getLinkWithID(barPackageName + "_to_" + bazPackageName), "Missing link bar --> baz");
        assertNotNull(response.getLinkWithID(bazPackageName), "Missing link baz");
        assertNotNull(response.getLinkWithID(bazPackageName + "_from_" + barPackageName), "Missing link baz <-- bar");
        assertNull(response.getLinkWithID(leftPackageName), "Unwanted link left");
        assertNull(response.getLinkWithID(leftPackageName + "_bidirectional_" + rightPackageName), "Unwanted link left <-> right");
        assertNull(response.getLinkWithID(rightPackageName), "Unwanted link right");
        assertNull(response.getLinkWithID(rightPackageName + "_bidirectional_" + leftPackageName), "Unwanted link right <-> left");
    }

    @Test
    void testFollowDownstreamLink() throws Exception {
        request.setParameter("start-includes", "/" + fooPackageName + "/");
        request.setParameter("scope", "package");
        request.setParameter("filter", "package");
        request.setParameter("maximum-inbound-depth", "0");
        request.setParameter("maximum-outbound-depth", "1");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNotNull(response.getLinkWith(fooPackageName), "Missing link to " + fooPackageName);
        assertNotNull(response.getLinkWith(barPackageName), "Missing link to " + barPackageName);
        assertNull(response.getLinkWith(bazPackageName), "Unwanted link to " + bazPackageName);

        assertNotNull(response.getLinkWithID(fooPackageName), "Missing link foo");
        assertNotNull(response.getLinkWithID(fooPackageName + "_to_" + barPackageName), "Missing link foo --> bar");
        assertNotNull(response.getLinkWithID(barPackageName), "Missing link bar");
        assertNotNull(response.getLinkWithID(barPackageName + "_from_" + fooPackageName), "Missing link bar <-- foo");
        assertNull(response.getLinkWithID(barPackageName + "_to_" + bazPackageName), "Unwanted link bar --> baz");
        assertNull(response.getLinkWithID(bazPackageName), "Unwanted link baz");
        assertNull(response.getLinkWithID(bazPackageName + "_from_" + barPackageName), "Unwanted link baz <-- bar");

        response = response.getLinkWithID(fooPackageName + "_to_" + barPackageName).click();

        assertNull(response.getLinkWith(fooPackageName), "Unwanted link to " + fooPackageName);
        assertNotNull(response.getLinkWith(barPackageName), "Missing link to " + barPackageName);
        assertNotNull(response.getLinkWith(bazPackageName), "Missing link to " + bazPackageName);

        assertNull(response.getLinkWithID(fooPackageName), "Unwanted link foo");
        assertNull(response.getLinkWithID(fooPackageName + "_to_" + barPackageName), "Unwanted link foo --> bar");
        assertNotNull(response.getLinkWithID(barPackageName), "Missing link bar");
        assertNull(response.getLinkWithID(barPackageName + "_from_" + fooPackageName), "Unwanted link bar <-- foo");
        assertNotNull(response.getLinkWithID(barPackageName + "_to_" + bazPackageName), "Missing link bar --> baz");
        assertNotNull(response.getLinkWithID(bazPackageName), "Missing link baz");
        assertNotNull(response.getLinkWithID(bazPackageName + "_from_" + barPackageName), "Missing link baz <-- bar");
    }
}
