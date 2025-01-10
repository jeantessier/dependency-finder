package com.jeantessier;

import org.jmock.Sequence;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.extension.RegisterExtension;

public class MockObjectTestCase {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    protected <T> T mock(Class<T> typeToMock) {
        return context.mock(typeToMock);
    }

    protected <T> T mock(Class<T> typeToMock, String name) {
        return context.mock(typeToMock, name);
    }

    protected Sequence sequence(String name) {
        return context.sequence(name);
    }

    protected void checking(ExpectationBuilder expectations) {
        context.checking(expectations);
    }
}
