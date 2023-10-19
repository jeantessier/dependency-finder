package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

public class TestBootstrapMethod extends TestAttributeBase {
    private BootstrapMethod sut;

    protected void setUp() throws Exception {
        super.setUp();

        expectReadNumArguments(0);

        sut = new BootstrapMethod(mockConstantPool, mockIn);
    }

    public void testGetArguments() throws Exception {
        assertEquals("arguments", 0, sut.getArguments().size());
    }

    public void testAccept() throws Exception {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitBootstrapMethod(sut);
        }});

        sut.accept(mockVisitor);
    }
}
