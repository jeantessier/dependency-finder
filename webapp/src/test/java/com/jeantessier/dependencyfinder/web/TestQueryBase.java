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

public abstract class TestQueryBase extends TestBase {
    @Test
    void testFormSubmit() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);

        assertEquals(1, response.getForms().length, "Nb forms");
        WebForm form = response.getForms()[0];
        assertEquals(1, form.getSubmitButtons().length, "Nb submit buttons");
        SubmitButton button = form.getSubmitButtons()[0];

        response = form.submit(button);

        assertNotNull(response.getLinkWith(fooPackageName), "Missing link to " + fooPackageName);
        assertNull(response.getLinkWith(fooClassName), "Unwanted link to " + fooClassName);
        assertNull(response.getLinkWith(fooFeatureName), "Unwanted link to " + fooFeatureName);
        assertNotNull(response.getLinkWith(barPackageName), "Missing link to " + barPackageName);
        assertNull(response.getLinkWith(barClassName), "Unwanted link to " + barClassName);
        assertNull(response.getLinkWith(barFeatureName), "Unwanted link to " + barFeatureName);
        assertNotNull(response.getLinkWith(bazPackageName), "Missing link to " + bazPackageName);
        assertNull(response.getLinkWith(bazClassName), "Unwanted link to " + bazClassName);
        assertNull(response.getLinkWith(bazFeatureName), "Unwanted link to " + bazFeatureName);
        assertNotNull(response.getLinkWith(leftPackageName), "Missing link to " + leftPackageName);
        assertNull(response.getLinkWith(leftClassName), "Unwanted link to " + leftClassName);
        assertNull(response.getLinkWith(leftFeatureName), "Unwanted link to " + leftFeatureName);
        assertNotNull(response.getLinkWith(rightPackageName), "Missing link to " + rightPackageName);
        assertNull(response.getLinkWith(rightClassName), "Unwanted link to " + rightClassName);
        assertNull(response.getLinkWith(rightFeatureName), "Unwanted link to " + rightFeatureName);
    }

    @Test
    void testDirectQuery() throws Exception {
        request.setParameter("scope-includes", "//");
        request.setParameter("package-scope", "on");
        request.setParameter("filter-includes", "//");
        request.setParameter("package-filter", "on");
        request.setParameter("show-inbounds", "on");
        request.setParameter("show-outbounds", "on");
        request.setParameter("show-empty-nodes", "on");
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
        assertNotNull(response.getLinkWith(leftPackageName), "Missing link to " + leftPackageName);
        assertNull(response.getLinkWith(leftClassName), "Unwanted link to " + leftClassName);
        assertNull(response.getLinkWith(leftFeatureName), "Unwanted link to " + leftFeatureName);
        assertNotNull(response.getLinkWith(rightPackageName), "Missing link to " + rightPackageName);
        assertNull(response.getLinkWith(rightClassName), "Unwanted link to " + rightClassName);
        assertNull(response.getLinkWith(rightFeatureName), "Unwanted link to " + rightFeatureName);

        assertNotNull(response.getLinkWithID(fooPackageName), "Missing link foo");
        assertNotNull(response.getLinkWithID(fooPackageName + "_to_" + barPackageName), "Missing link foo --> bar");
        assertNotNull(response.getLinkWithID(barPackageName), "Missing link bar");
        assertNotNull(response.getLinkWithID(barPackageName + "_from_" + fooPackageName), "Missing link bar <-- foo");
        assertNotNull(response.getLinkWithID(barPackageName + "_to_" + bazPackageName), "Missing link bar --> baz");
        assertNotNull(response.getLinkWithID(bazPackageName), "Missing link baz");
        assertNotNull(response.getLinkWithID(bazPackageName + "_from_" + barPackageName), "Missing link baz <-- bar");
        assertNotNull(response.getLinkWithID(leftPackageName), "Missing link left");
        assertNotNull(response.getLinkWithID(leftPackageName + "_bidirectional_" + rightPackageName), "Missing link left <-> right");
        assertNotNull(response.getLinkWithID(rightPackageName), "Missing link right");
        assertNotNull(response.getLinkWithID(rightPackageName + "_bidirectional_" + leftPackageName), "Missing link right <-> left");
    }

    @Test
    void testFollowDependencyLink() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);

        WebForm form = response.getForms()[0];
        form.setCheckbox("package-scope", false);
        form.setCheckbox("feature-scope", true);
        form.setCheckbox("package-filter", false);
        form.setCheckbox("feature-filter", true);

        response = form.submit(form.getSubmitButtons()[0]);
        response = response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName).click();

        assertNull(response.getLinkWithID(fooPackageName), "Unwanted link foo");
        assertNotNull(response.getLinkWithID(barPackageName), "Missing link bar");
        assertNotNull(response.getLinkWithID(barClassName), "Missing link bar.Bar");
        assertNotNull(response.getLinkWithID(barFeatureName), "Missing link bar.Bar.bar()");
        assertNotNull(response.getLinkWithID(barFeatureName + "_from_" + fooFeatureName), "Missing link bar.Bar.bar() <-- foo.Foo.foo()");
        assertNotNull(response.getLinkWithID(barFeatureName + "_to_" + bazFeatureName), "Missing link bar.Bar.bar() --> baz.Baz.baz()");
        assertNull(response.getLinkWithID(bazPackageName), "Unwanted link baz");
        assertNull(response.getLinkWithID(leftPackageName), "Unwanted link left");
        assertNull(response.getLinkWithID(rightPackageName), "Unwanted link right");
    }

    @Test
    void testNarrowScope() throws Exception {
        factory.createFeature(foo2FeatureName);

        context.service();
        WebResponse response = client.getResponse(request);

        WebForm form = response.getForms()[0];
        form.setCheckbox("package-scope", false);
        form.setCheckbox("feature-scope", true);
        form.setCheckbox("package-filter", false);
        form.setCheckbox("feature-filter", true);

        response = form.submit(form.getSubmitButtons()[0]);
        response = response.getLinkWithID(fooFeatureName).click();

        assertNotNull(response.getLinkWithID(fooPackageName), "Missing link foo");
        assertNotNull(response.getLinkWithID(fooClassName), "Missing link foo.Foo");
        assertNotNull(response.getLinkWithID(fooFeatureName), "Missing link foo.Foo.foo()");
        assertNotNull(response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName), "Missing link foo.Foo.foo() --> bar.Bar.bar()");
        assertNull(response.getLinkWithID(foo2ClassName), "Unwanted link foo.Foo2");
        assertNull(response.getLinkWithID(barPackageName), "Unwanted link bar");
        assertNull(response.getLinkWithID(bazPackageName), "Unwanted link baz");
        assertNull(response.getLinkWithID(leftPackageName), "Unwanted link left");
        assertNull(response.getLinkWithID(rightPackageName), "Unwanted link right");
    }

    @Test
    void testNarrowThanWidenScope() throws Exception {
        factory.createFeature(foo2FeatureName);

        context.service();
        WebResponse response = client.getResponse(request);

        WebForm form = response.getForms()[0];
        form.setCheckbox("package-scope", false);
        form.setCheckbox("feature-scope", true);
        form.setCheckbox("package-filter", false);
        form.setCheckbox("feature-filter", true);

        response = form.submit(form.getSubmitButtons()[0]);
        response = response.getLinkWithID(fooFeatureName).click();
        response = response.getLinkWithID(fooPackageName).click();

        assertNotNull(response.getLinkWithID(fooPackageName), "Missing link foo");
        assertNotNull(response.getLinkWithID(fooClassName), "Missing link foo.Foo");
        assertNotNull(response.getLinkWithID(fooFeatureName), "Missing link foo.Foo.foo()");
        assertNotNull(response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName), "Missing link foo.Foo.foo() --> bar.Bar.bar()");
        assertNotNull(response.getLinkWithID(foo2ClassName), "Missing link foo.Foo2");
        assertNotNull(response.getLinkWithID(foo2FeatureName), "Missing link foo.Foo2.foo2()");
        assertNull(response.getLinkWithID(barPackageName), "Unwanted link bar");
        assertNull(response.getLinkWithID(bazPackageName), "Unwanted link baz");
        assertNull(response.getLinkWithID(leftPackageName), "Unwanted link left");
        assertNull(response.getLinkWithID(rightPackageName), "Unwanted link right");
    }

    @Test
    void testEscapeSquareBrackets() throws Exception {
        String mainFeatureName = fooClassName + ".main(java.lang.String[])";
        factory.createFeature(mainFeatureName);

        context.service();
        WebResponse response = client.getResponse(request);

        WebForm form = response.getForms()[0];
        form.setCheckbox("package-scope", false);
        form.setCheckbox("feature-scope", true);
        form.setCheckbox("package-filter", false);
        form.setCheckbox("feature-filter", true);
        form.setParameter("scope-includes", "/\\[/");

        response = form.submit(form.getSubmitButtons()[0]);
        WebLink webLink = response.getLinkWithID(mainFeatureName);
        assertNotNull(webLink, "Missing link for " + mainFeatureName);
        response = webLink.click();
        assertEquals(200, response.getResponseCode(), "response code");
    }
}
