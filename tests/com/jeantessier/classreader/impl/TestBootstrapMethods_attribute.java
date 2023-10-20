package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

public class TestBootstrapMethods_attribute extends TestAttributeBase {
    private BootstrapMethods_attribute sut;

    protected void setUp() throws Exception {
        super.setUp();

        expectReadAttributeLength(2);
        expectReadU2(0);

        sut = new BootstrapMethods_attribute(mockConstantPool, mockOwner, mockIn);
    }

    public void testGetBootstrapMethods() throws Exception {
        assertEquals("bootstrap methods", 0, sut.getBootstrapMethods().size());
    }

    public void testGetAttributeName() {
        assertEquals(AttributeType.BOOTSTRAP_METHODS.getAttributeName(), sut.getAttributeName());
    }

    public void testAccept() throws Exception {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitBootstrapMethods_attribute(sut);
        }});

        sut.accept(mockVisitor);
    }
}
