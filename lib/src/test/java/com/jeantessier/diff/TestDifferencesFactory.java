package com.jeantessier.diff;

import com.jeantessier.classreader.*;
import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDifferencesFactory {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final String fullUniqueName = "test.Test.test(): void";

    private final Method_info oldMethod = context.mock(Method_info.class, "oldMethod");
    private final Classfile newClass = context.mock(Classfile.class, "newClass");
    private final Method_info newMethod = null;

    DifferenceStrategy strategy = context.mock(DifferenceStrategy.class);
    DifferencesFactory factory = new DifferencesFactory(strategy);

    @BeforeEach
    void setup() {
        context.checking(new Expectations() {{
            allowing (oldMethod).isConstructor();
                will(returnValue(false));
            allowing (oldMethod).getDeclaration();
        }});
    }

    @Test
    void testRemovedMethod() {
        // Given
        context.checking(new Expectations() {{
            oneOf (newClass).locateMethod(with(any(Predicate.class)));
                will(returnValue(null));
        }});

        // When
        var difference = factory.createFeatureDifferences(newClass, fullUniqueName, oldMethod, newMethod);

        // Then
        assertInstanceOf(MethodDifferences.class, difference);
        assertFalse(((MethodDifferences) difference).isInherited(), "should not be inherited");
    }

    @Test
    void testRemovedMethodIsInherited() {
        // Given
        Method_info inheritedMethod = context.mock(Method_info.class, "inheritedMethod");
        context.checking(new Expectations() {{
            oneOf (newClass).locateMethod(with(any(Predicate.class)));
                will(returnValue(inheritedMethod));
        }});

        // When
        var difference = factory.createFeatureDifferences(newClass, fullUniqueName, oldMethod, newMethod);

        // Then
        assertInstanceOf(MethodDifferences.class, difference);
        assertTrue(((MethodDifferences) difference).isInherited(), "should be inherited");
    }
}
