package com.jeantessier.classreader.impl;

import org.jmock.*;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Parameterized;

import java.io.DataInput;
import java.io.IOException;

import static junit.framework.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestBootstrapMethodWithOneArgument {
    @Parameters(name="BootstrapMethod with {0} argument")
    public static Object[][] data() {
        return new Object[][] {
                {"an integer", 1, com.jeantessier.classreader.Integer_info.class},
                {"a float", 2, com.jeantessier.classreader.Float_info.class},
                {"a long", 3, com.jeantessier.classreader.Long_info.class},
                {"a double", 4, com.jeantessier.classreader.Double_info.class},
                {"a Class", 5, com.jeantessier.classreader.Class_info.class},
                {"a String", 6, com.jeantessier.classreader.String_info.class},
                {"a MethodHandle", 7, com.jeantessier.classreader.MethodHandle_info.class},
                {"a MethodType", 8, com.jeantessier.classreader.MethodType_info.class},
                {"a Dynamic", 9, com.jeantessier.classreader.Dynamic_info.class},
        };
    }

    @Parameterized.Parameter(0)
    public String label;

    @Parameterized.Parameter(1)
    public int argumentIndex;

    @Parameterized.Parameter(2)
    public Class<? extends ConstantPoolEntry> argumentClass;

    private Mockery context;

    private ConstantPool mockConstantPool;
    private DataInput mockIn;
    private com.jeantessier.classreader.ConstantPoolEntry mockArgument;

    private BootstrapMethod sut;

    @Before
    public void setUp() throws IOException {
        context = new Mockery();
        context.setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        mockConstantPool = context.mock(ConstantPool.class);
        mockIn = context.mock(DataInput.class);
        mockArgument = context.mock(argumentClass);

        context.checking(new Expectations() {{
            // num arguments
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(1));
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(argumentIndex));
            // Lookup during construction
            oneOf (mockConstantPool).get(argumentIndex);
                will(returnValue(mockArgument));
        }});

        sut = new BootstrapMethod(mockConstantPool, mockIn);
    }

    @Test
    public void testArgument() {
        context.checking(new Expectations() {{
            oneOf (mockConstantPool).get(argumentIndex);
                will(returnValue(mockArgument));
        }});

        assertEquals("num arguments", 1, sut.getArguments().size());
        assertSame("argument", mockArgument, sut.getArguments().stream().findFirst().get());
    }
}
