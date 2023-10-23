package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

public class TestStackMapTable_attribute extends TestAttributeBase {
    private StackMapTable_attribute sut;

    protected void setUp() throws Exception {
        super.setUp();

        final StackMapFrameFactory mockStackMapFrameFactory = mock(StackMapFrameFactory.class);

        expectReadAttributeLength(2);
        expectReadU2(0);

        sut = new StackMapTable_attribute(mockStackMapFrameFactory, mockConstantPool, mockOwner, mockIn);
    }

    public void testGetEntries() throws Exception {
        assertEquals("entries", 0, sut.getEntries().size());
    }

    public void testGetAttributeName() {
        assertEquals(AttributeType.STACK_MAP_TABLE.getAttributeName(), sut.getAttributeName());
    }

    public void testAccept() throws Exception {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitStackMapTable_attribute(sut);
        }});

        sut.accept(mockVisitor);
    }
}
