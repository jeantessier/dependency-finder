package com.jeantessier.dependencyfinder.web;

import junit.framework.*;

public class TestAll extends TestCase {
    public static Test suite() {
        TestSuite result = new TestSuite();

        result.addTestSuite(TestQuery.class);

        return result;
    }
}
