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

public abstract class TestQueryBase extends TestBase {
    public void testFormSubmit() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);

        assertEquals("Nb forms", 1, response.getForms().length);
        WebForm form = response.getForms()[0];
        assertEquals("Nb submit buttons", 1, form.getSubmitButtons().length);
        SubmitButton button = form.getSubmitButtons()[0];

        response = form.submit(button);

        assertNotNull("Missing link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNull("Unwanted link to " + fooClassName, response.getLinkWith(fooClassName));
        assertNull("Unwanted link to " + fooFeatureName, response.getLinkWith(fooFeatureName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNull("Unwanted link to " + barClassName, response.getLinkWith(barClassName));
        assertNull("Unwanted link to " + barFeatureName, response.getLinkWith(barFeatureName));
        assertNotNull("Missing link to " + bazPackageName, response.getLinkWith(bazPackageName));
        assertNull("Unwanted link to " + bazClassName, response.getLinkWith(bazClassName));
        assertNull("Unwanted link to " + bazFeatureName, response.getLinkWith(bazFeatureName));
        assertNotNull("Missing link to " + leftPackageName, response.getLinkWith(leftPackageName));
        assertNull("Unwanted link to " + leftClassName, response.getLinkWith(leftClassName));
        assertNull("Unwanted link to " + leftFeatureName, response.getLinkWith(leftFeatureName));
        assertNotNull("Missing link to " + rightPackageName, response.getLinkWith(rightPackageName));
        assertNull("Unwanted link to " + rightClassName, response.getLinkWith(rightClassName));
        assertNull("Unwanted link to " + rightFeatureName, response.getLinkWith(rightFeatureName));
    }

    public void testDirectQuery() throws Exception {
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

        assertNotNull("Missing link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNull("Unwanted link to " + fooClassName, response.getLinkWith(fooClassName));
        assertNull("Unwanted link to " + fooFeatureName, response.getLinkWith(fooFeatureName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNull("Unwanted link to " + barClassName, response.getLinkWith(barClassName));
        assertNull("Unwanted link to " + barFeatureName, response.getLinkWith(barFeatureName));
        assertNotNull("Missing link to " + bazPackageName, response.getLinkWith(bazPackageName));
        assertNull("Unwanted link to " + bazClassName, response.getLinkWith(bazClassName));
        assertNull("Unwanted link to " + bazFeatureName, response.getLinkWith(bazFeatureName));
        assertNotNull("Missing link to " + leftPackageName, response.getLinkWith(leftPackageName));
        assertNull("Unwanted link to " + leftClassName, response.getLinkWith(leftClassName));
        assertNull("Unwanted link to " + leftFeatureName, response.getLinkWith(leftFeatureName));
        assertNotNull("Missing link to " + rightPackageName, response.getLinkWith(rightPackageName));
        assertNull("Unwanted link to " + rightClassName, response.getLinkWith(rightClassName));
        assertNull("Unwanted link to " + rightFeatureName, response.getLinkWith(rightFeatureName));

        assertNotNull("Missing link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link foo --> bar", response.getLinkWithID(fooPackageName + "_to_" + barPackageName));
        assertNotNull("Missing link bar", response.getLinkWithID(barPackageName));
        assertNotNull("Missing link bar <-- foo", response.getLinkWithID(barPackageName + "_from_" + fooPackageName));
        assertNotNull("Missing link bar --> baz", response.getLinkWithID(barPackageName + "_to_" + bazPackageName));
        assertNotNull("Missing link baz", response.getLinkWithID(bazPackageName));
        assertNotNull("Missing link baz <-- bar", response.getLinkWithID(bazPackageName + "_from_" + barPackageName));
        assertNotNull("Missing link left", response.getLinkWithID(leftPackageName));
        assertNotNull("Missing link left <-> right", response.getLinkWithID(leftPackageName + "_bidirectional_" + rightPackageName));
        assertNotNull("Missing link right", response.getLinkWithID(rightPackageName));
        assertNotNull("Missing link right <-> left", response.getLinkWithID(rightPackageName + "_bidirectional_" + leftPackageName));
    }

    public void testFollowDependencyLink() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);

        WebForm form = response.getForms()[0];
        form.setCheckbox("package-scope", false);
        form.setCheckbox("feature-scope", true);
        form.setCheckbox("package-filter", false);
        form.setCheckbox("feature-filter", true);

        response = form.submit(form.getSubmitButtons()[0]);
        response = response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName).click();

        assertNull("Unwanted link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link bar", response.getLinkWithID(barPackageName));
        assertNotNull("Missing link bar.Bar", response.getLinkWithID(barClassName));
        assertNotNull("Missing link bar.Bar.bar()", response.getLinkWithID(barFeatureName));
        assertNotNull("Missing link bar.Bar.bar() <-- foo.Foo.foo()", response.getLinkWithID(barFeatureName + "_from_" + fooFeatureName));
        assertNotNull("Missing link bar.Bar.bar() --> baz.Baz.baz()", response.getLinkWithID(barFeatureName + "_to_" + bazFeatureName));
        assertNull("Unwanted link baz", response.getLinkWithID(bazPackageName));
        assertNull("Unwanted link left", response.getLinkWithID(leftPackageName));
        assertNull("Unwanted link right", response.getLinkWithID(rightPackageName));
    }

    public void testNarrowScope() throws Exception {
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

        assertNotNull("Missing link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link foo.Foo", response.getLinkWithID(fooClassName));
        assertNotNull("Missing link foo.Foo.foo()", response.getLinkWithID(fooFeatureName));
        assertNotNull("Missing link foo.Foo.foo() --> bar.Bar.bar()", response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName));
        assertNull("Unwanted link foo.Foo2", response.getLinkWithID(foo2ClassName));
        assertNull("Unwanted link bar", response.getLinkWithID(barPackageName));
        assertNull("Unwanted link baz", response.getLinkWithID(bazPackageName));
        assertNull("Unwanted link left", response.getLinkWithID(leftPackageName));
        assertNull("Unwanted link right", response.getLinkWithID(rightPackageName));
    }

    public void testNarrowThanWidenScope() throws Exception {
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

        assertNotNull("Missing link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link foo.Foo", response.getLinkWithID(fooClassName));
        assertNotNull("Missing link foo.Foo.foo()", response.getLinkWithID(fooFeatureName));
        assertNotNull("Missing link foo.Foo.foo() --> bar.Bar.bar()", response.getLinkWithID(fooFeatureName + "_to_" + barFeatureName));
        assertNotNull("Missing link foo.Foo2", response.getLinkWithID(foo2ClassName));
        assertNotNull("Missing link foo.Foo2.foo2()", response.getLinkWithID(foo2FeatureName));
        assertNull("Unwanted link bar", response.getLinkWithID(barPackageName));
        assertNull("Unwanted link baz", response.getLinkWithID(bazPackageName));
        assertNull("Unwanted link left", response.getLinkWithID(leftPackageName));
        assertNull("Unwanted link right", response.getLinkWithID(rightPackageName));
    }

    public void testEscapeSquareBrackets() throws Exception {
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
        assertNotNull("Missing link for " + mainFeatureName, webLink);
        response = webLink.click();
        assertEquals("response code", 200, response.getResponseCode());
    }
}
