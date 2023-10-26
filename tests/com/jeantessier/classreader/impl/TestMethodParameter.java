package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

import java.io.IOException;

public class TestMethodParameter extends TestAttributeBase {
    private static final int ACCESS_FLAGS = 456;

    public void testCreateNamelessMethodParameter() throws IOException {
        var sut = createMethodParameter();

        assertEquals("name", null, sut.getName());
    }

    public void testCreateNamedMethodParameter() throws IOException {
        final int nameIndex = 123;
        final String encodedName = "LAbc;";
        final String expectedName = "Abc";

        var sut = createMethodParameter(nameIndex, encodedName);
        expectLookupUtf8(nameIndex, encodedName);

        assertEquals("name", expectedName, sut.getName());
    }

    public void testAccept() throws IOException {
        var sut = createMethodParameter();

        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitMethodParameter(sut);
        }});

        sut.accept(mockVisitor);
    }

    private MethodParameter createMethodParameter() throws IOException {
        expectReadU2(0);
        expectReadU2(ACCESS_FLAGS);

        return new MethodParameter(mockConstantPool, mockIn);
    }

    private MethodParameter createMethodParameter(final int nameIndex, final String encodedName) throws IOException {
        expectReadU2(nameIndex);
        expectLookupUtf8(nameIndex, encodedName, "lookup during construction");
        expectReadU2(ACCESS_FLAGS);

        return new MethodParameter(mockConstantPool, mockIn);
    }
}
