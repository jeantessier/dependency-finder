/*
 *  Copyright (c) 2001-2024, Jean Tessier
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.Visitable;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.io.DataInput;
import java.io.IOException;

public abstract class TestAttributeBase extends MockObjectTestCase {
    protected Classfile mockClassfile;
    protected ConstantPool mockConstantPool;
    protected Visitable mockOwner;
    protected DataInput mockIn;

    protected Sequence dataReads;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        mockClassfile = mock(Classfile.class);
        mockConstantPool = mock(ConstantPool.class);
        mockOwner = mock(Visitable.class);
        mockIn = mock(DataInput.class);

        dataReads = sequence("dataReads");
    }

    protected void allowingLookupModule(final int index, final String value) {
        allowingLookupModule(index, value, mock(Module_info.class));
    }

    protected void allowingLookupModule(final int index, final String value, String mockName) {
        allowingLookupModule(index, value, mock(Module_info.class, mockName));
    }

    private void allowingLookupModule(final int index, final String value, final Module_info mockModule_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockModule_info));
            allowing (mockModule_info).getName();
                will(returnValue(value));
        }});
    }

    protected void allowingLookupPackage(final int index, final String value) {
        allowingLookupPackage(index, value, mock(Package_info.class));
    }

    protected void allowingLookupPackage(final int index, final String value, String mockName) {
        allowingLookupPackage(index, value, mock(Package_info.class, mockName));
    }

    private void allowingLookupPackage(final int index, final String value, final Package_info mockPackage_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockPackage_info));
            allowing (mockPackage_info).getName();
                will(returnValue(value));
        }});
    }

    protected void allowingLookupUtf8(final int index, final String value) {
        allowingLookupUtf8(index, value, mock(UTF8_info.class));
    }

    protected void allowingLookupUtf8(final int index, final String value, String mockName) {
        allowingLookupUtf8(index, value, mock(UTF8_info.class, mockName));
    }

    private void allowingLookupUtf8(final int index, final String value, final UTF8_info mockUtf8_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockUtf8_info));
            allowing (mockUtf8_info).getValue();
                will(returnValue(value));
        }});
    }

    protected void allowingLookupClass(final int index, final String value) {
        allowingLookupClass(index, value, mock(Class_info.class));
    }

    protected void allowingLookupClass(final int index, final String value, String mockName) {
        allowingLookupClass(index, value, mock(Class_info.class, mockName));
    }

    private void allowingLookupClass(final int index, final String value, final Class_info mockClass_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockClass_info));
            allowing (mockClass_info).getName();
                will(returnValue(value));
        }});
    }

    protected void expectReadAttributeLength(final int length) throws IOException {
        expectReadU4(length);
    }

    protected void expectReadNumAnnotations(int numAnnotations) throws IOException {
        expectReadU2(numAnnotations);
    }

    protected void expectReadNumArguments(int numArguments) throws IOException {
        expectReadU2(numArguments);
    }

    protected void expectReadNumParameters(int numParameters) throws IOException {
        expectReadU1(numParameters);
    }

    protected void expectReadAnnotation(int typeIndex, int numElementValuePairs) throws IOException {
        expectReadTypeIndex(typeIndex);
        expectReadNumElementValuePairs(numElementValuePairs);
    }

    protected void expectReadTypeIndex(int typeIndex) throws IOException {
        expectReadU2(typeIndex);
    }

    protected void expectReadNumElementValuePairs(int numElementValuePairs) throws IOException {
        expectReadU2(numElementValuePairs);
    }

    protected void expectReadU1(final int i) throws IOException {
        checking(new Expectations() {{
            oneOf (mockIn).readUnsignedByte();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadU2(final int i) throws IOException {
        checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadU4(final int i) throws IOException {
        checking(new Expectations() {{
            oneOf (mockIn).readInt();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadFully() throws IOException {
        checking(new Expectations() {{
            oneOf (mockIn).readFully((byte[]) with(any(Object.class)));
                inSequence(dataReads);
        }});
    }

    protected void expectReadUtf(final String s) throws IOException {
        checking(new Expectations() {{
            oneOf (mockIn).readUTF();
                inSequence(dataReads);
                will(returnValue(s));
        }});
    }

    protected void expectLookupModule(final int index, final String value) {
        expectLookupModule(index, value, mock(Module_info.class));
    }

    protected void expectLookupModule(final int index, final String value, String mockName) {
        expectLookupModule(index, value, mock(Module_info.class, mockName));
    }

    private void expectLookupModule(final int index, final String value, final Module_info mockModule_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockModule_info));
            oneOf (mockModule_info).getName();
                will(returnValue(value));
        }});
    }

    protected void expectLookupPackage(final int index, final String value) {
        expectLookupPackage(index, value, mock(Package_info.class));
    }

    protected void expectLookupPackage(final int index, final String value, String mockName) {
        expectLookupPackage(index, value, mock(Package_info.class, mockName));
    }

    private void expectLookupPackage(final int index, final String value, final Package_info mockPackage_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockPackage_info));
            oneOf (mockPackage_info).getName();
                will(returnValue(value));
        }});
    }

    protected void expectLookupClass(final int index, final String value) {
        expectLookupClass(index, value, mock(Class_info.class));
    }

    protected void expectLookupClass(final int index, final String value, String mockName) {
        expectLookupClass(index, value, mock(Class_info.class, mockName));
    }

    private void expectLookupClass(final int index, final String value, final Class_info mockClass_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockClass_info));
            oneOf (mockClass_info).getName();
                will(returnValue(value));
        }});
    }

    protected void expectLookupNameAndType(final int index, final String name, final String type) {
        expectLookupNameAndType(index, name, type, mock(NameAndType_info.class));
    }

    protected void expectLookupNameAndType(final int index, final String name, final String type, String mockName) {
        expectLookupNameAndType(index, name, type, mock(NameAndType_info.class, mockName));
    }

    private void expectLookupNameAndType(final int index, final String name, final String type, final NameAndType_info mockNameAndType_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockNameAndType_info));
            oneOf (mockNameAndType_info).getName();
                will(returnValue(name));
            oneOf (mockNameAndType_info).getType();
                will(returnValue(type));
        }});
    }

    protected void expectLookupInteger(int index, int value) {
        expectLookupInteger(index, value, mock(Integer_info.class));
    }

    protected void expectLookupInteger(int index, int value, String mockName) {
        expectLookupInteger(index, value, mock(Integer_info.class, mockName));
    }

    private void expectLookupInteger(final int index, final int value, final Integer_info mockInteger_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockInteger_info));
            oneOf (mockInteger_info).getValue();
                will(returnValue(value));
        }});
    }

    protected void expectLookupLong(final int index, final long value) {
        expectLookupLong(index, value, mock(Long_info.class));
    }

    protected void expectLookupLong(final int index, final long value, String mockName) {
        expectLookupLong(index, value, mock(Long_info.class, mockName));
    }

    private void expectLookupLong(final int index, final long value, final Long_info mockLong_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockLong_info));
            oneOf (mockLong_info).getValue();
                will(returnValue(value));
        }});
    }

    protected void expectLookupFloat(final int index, final float value) {
        expectLookupFloat(index, value, mock(Float_info.class));
    }

    protected void expectLookupFloat(final int index, final float value, String mockName) {
        expectLookupFloat(index, value, mock(Float_info.class, mockName));
    }

    private void expectLookupFloat(final int index, final float value, final Float_info mockFloat_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockFloat_info));
            oneOf (mockFloat_info).getValue();
                will(returnValue(value));
        }});
    }

    protected void expectLookupDouble(final int index, final double value) {
        expectLookupDouble(index, value, mock(Double_info.class));
    }

    protected void expectLookupDouble(final int index, final double value, String mockName) {
        expectLookupDouble(index, value, mock(Double_info.class, mockName));
    }

    private void expectLookupDouble(final int index, final double value, final Double_info mockDouble_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockDouble_info));
            oneOf (mockDouble_info).getValue();
                will(returnValue(value));
        }});
    }

    protected void expectLookupString(final int index, final String value) {
        expectLookupString(index, value, mock(String_info.class));
    }

    protected void expectLookupString(final int index, final String value, String mockName) {
        expectLookupString(index, value, mock(String_info.class, mockName));
    }

    private void expectLookupString(final int index, final String value, final String_info mockString_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockString_info));
            oneOf (mockString_info).getValue();
                will(returnValue(value));
        }});
    }

    protected void expectLookupUtf8(int index, String value) {
        expectLookupUtf8(index, value, mock(UTF8_info.class));
    }

    protected void expectLookupUtf8(int index, String value, String mockName) {
        expectLookupUtf8(index, value, mock(UTF8_info.class, mockName));
    }

    private void expectLookupUtf8(final int index, final String value, final UTF8_info mockUtf8_info) {
        checking(new Expectations() {{
            oneOf (mockConstantPool).get(index);
                will(returnValue(mockUtf8_info));
            oneOf (mockUtf8_info).getValue();
                will(returnValue(value));
        }});
    }
}
