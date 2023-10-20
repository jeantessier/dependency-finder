package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

public class TestBootstrapMethod extends TestAttributeBase {
    private static final int BOOTSTRAP_METHOD_REF = 123;

    private BootstrapMethod sut;

    protected void setUp() throws Exception {
        super.setUp();

        var mockBootstrapMethods = mock(BootstrapMethods_attribute.class);
        var mockBootstrapMethod = mock(MethodHandle_info.class);

        expectReadU2(BOOTSTRAP_METHOD_REF);
        checking(new Expectations() {{
            allowing (mockBootstrapMethods).getConstantPool();
                will(returnValue(mockConstantPool));
            oneOf (mockConstantPool).get(BOOTSTRAP_METHOD_REF);
                will(returnValue(mockBootstrapMethod));
        }});

        expectReadNumArguments(0);

        sut = new BootstrapMethod(mockBootstrapMethods, mockIn);
    }

    public void testGetArguments() {
        assertEquals("arguments", 0, sut.getArguments().size());
    }

    public void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitBootstrapMethod(sut);
        }});

        sut.accept(mockVisitor);
    }
}
