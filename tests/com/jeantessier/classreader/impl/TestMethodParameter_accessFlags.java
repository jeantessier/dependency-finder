package com.jeantessier.classreader.impl;

import org.jmock.*;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Parameterized;

import java.io.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class TestMethodParameter_accessFlags {
    @Parameters(name="Method parameter with access flags {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"FINAL", 0x0010, true, false, false},
                {"SYNTHETIC", 0x1000, false, true, false},
                {"MANDATED", 0x8000, false, false, true},
                {"all of them", 0x9010, true, true, true},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int accessFlags;

    @Parameter(2)
    public boolean isFinal;

    @Parameter(3)
    public boolean isSynthetic;

    @Parameter(4)
    public boolean isMandated;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private ConstantPool mockConstantPool;
    private DataInput mockIn;

    private MethodParameter sut;

    @Before
    public void setUp() throws IOException {
        context.setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        mockConstantPool = context.mock(ConstantPool.class);
        mockIn = context.mock(DataInput.class);

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(0));
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(accessFlags));
        }});

        sut = new MethodParameter(mockConstantPool, mockIn);
    }

    @Test
    public void testIsFinal() {
        assertEquals(label, isFinal, sut.isFinal());
    }

    @Test
    public void testIsSynthetic() {
        assertEquals(label, isSynthetic, sut.isSynthetic());
    }

    @Test
    public void testIsMandated() {
        assertEquals(label, isMandated, sut.isMandated());
    }
}
