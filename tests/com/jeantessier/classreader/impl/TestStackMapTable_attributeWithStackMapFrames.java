package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

public class TestStackMapTable_attributeWithStackMapFrames extends TestAttributeBase {
    private StackMapFrameFactory mockStackMapFrameFactory;

    protected void setUp() throws Exception {
        super.setUp();

        mockStackMapFrameFactory = mock(StackMapFrameFactory.class);

        expectReadAttributeLength(2);
    }

    public void testOneEntry() throws Exception {
        // Given
        final StackMapFrame mockStackMapFrame = mock(StackMapFrame.class);
        expectReadU2(1);
        checking(new Expectations() {{
            oneOf (mockStackMapFrameFactory).create(mockConstantPool, mockIn);
                will(returnValue(mockStackMapFrame));
        }});

        // When
        var sut = new StackMapTable_attribute(mockStackMapFrameFactory, mockConstantPool, mockOwner, mockIn);

        // Then
        assertEquals("entries", 1, sut.getEntries().size());
        assertSame(mockStackMapFrame, sut.getEntries().stream().findFirst().orElseThrow());
    }

    public void testMultipleEntries() throws Exception {
        // Given
        final StackMapFrame mockStackMapFrame1 = mock(StackMapFrame.class, "first frame");
        final StackMapFrame mockStackMapFrame2 = mock(StackMapFrame.class, "second frame");
        expectReadU2(2);
        checking(new Expectations() {{
            oneOf (mockStackMapFrameFactory).create(mockConstantPool, mockIn);
                will(returnValue(mockStackMapFrame1));
            oneOf (mockStackMapFrameFactory).create(mockConstantPool, mockIn);
                will(returnValue(mockStackMapFrame2));
        }});

        // When
        var sut = new StackMapTable_attribute(mockStackMapFrameFactory, mockConstantPool, mockOwner, mockIn);

        // Then
        assertEquals("entries", 2, sut.getEntries().size());
        assertSame(mockStackMapFrame1, sut.getEntries().stream().findFirst().orElseThrow());
        assertSame(mockStackMapFrame2, sut.getEntries().stream().skip(1).findFirst().orElseThrow());
    }
}
