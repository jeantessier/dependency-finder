package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Integer_info;
import org.jmock.Expectations;

public class TestBootstrapMethodWithMultipleArguments extends TestAttributeBase {
    private static final int BOOTSTRAP_METHOD_REF = 123;
    private static final int FIRST_ARGUMENT_INDEX = 4;
    private static final int FIRST_ARGUMENT_VALUE = 1234;
    private static final int SECOND_ARGUMENT_INDEX = 12;
    private static final String SECOND_ARGUMENT_VALUE = "abc def";
    private static final int THIRD_ARGUMENT_INDEX = 42;
    private static final String THIRD_ARGUMENT_VALUE = "Abc";

    private Integer_info firstArgument;
    private String_info secondArgument;
    private Class_info thirdArgument;

    private BootstrapMethod sut;

    protected void setUp() throws Exception {
        super.setUp();

        var mockBootstrapMethods = mock(BootstrapMethods_attribute.class);
        var mockBootstrapMethod = mock(MethodHandle_info.class);

        checking(new Expectations() {{
            allowing (mockBootstrapMethods).getConstantPool();
                will(returnValue(mockConstantPool));
            allowing (mockConstantPool).get(BOOTSTRAP_METHOD_REF);
                will(returnValue(mockBootstrapMethod));
        }});

        expectReadU2(BOOTSTRAP_METHOD_REF);
        expectReadNumArguments(3);

        firstArgument = mock(Integer_info.class);
        expectReadU2(FIRST_ARGUMENT_INDEX);
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(FIRST_ARGUMENT_INDEX);
                will(returnValue(firstArgument));
        }});

        secondArgument = mock(String_info.class);
        expectReadU2(SECOND_ARGUMENT_INDEX);
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(SECOND_ARGUMENT_INDEX);
            will(returnValue(secondArgument));
        }});

        thirdArgument = mock(Class_info.class);
        expectReadU2(THIRD_ARGUMENT_INDEX);
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(THIRD_ARGUMENT_INDEX);
            will(returnValue(thirdArgument));
        }});

        sut = new BootstrapMethod(mockBootstrapMethods, mockIn);
    }

    public void testNumArguments() {
        assertEquals("num arguments", 3, sut.getArguments().size());
    }

    public void testIntegerArguments() {
        checking(new Expectations() {{
            oneOf (firstArgument).getValue();
            will(returnValue(FIRST_ARGUMENT_VALUE));
        }});

        int actualArgument = sut.getArguments().stream()
                .filter(argument -> argument instanceof Integer_info)
                .map(argument -> (Integer_info) argument)
                .map(Integer_info::getValue)
                .findFirst()
                .orElseThrow();
        assertEquals("first argument should be an int", FIRST_ARGUMENT_VALUE, actualArgument);
    }

    public void testStringArguments() {
        checking(new Expectations() {{
            oneOf (secondArgument).getValue();
            will(returnValue(SECOND_ARGUMENT_VALUE));
        }});

        String actualArgument = sut.getArguments().stream()
                .filter(argument -> argument instanceof String_info)
                .map(argument -> (String_info) argument)
                .map(String_info::getValue)
                .findFirst()
                .orElseThrow();
        assertEquals("second argument should be a String", SECOND_ARGUMENT_VALUE, actualArgument);
    }

    public void testClassArguments() {
        checking(new Expectations() {{
            oneOf (thirdArgument).getName();
            will(returnValue(THIRD_ARGUMENT_VALUE));
        }});

        String actualArgument = sut.getArguments().stream()
                .filter(argument -> argument instanceof Class_info)
                .map(argument -> (Class_info) argument)
                .map(Class_info::getName)
                .findFirst()
                .orElseThrow();
        assertEquals("third argument should be a class", THIRD_ARGUMENT_VALUE, actualArgument);
    }
}
