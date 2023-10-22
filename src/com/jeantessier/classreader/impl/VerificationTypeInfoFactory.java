package com.jeantessier.classreader.impl;

import org.apache.log4j.Logger;

import java.io.*;

public class VerificationTypeInfoFactory {
    public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
        VerificationTypeInfo result;

        int tag = in.readUnsignedByte();
        VerificationType verificationType = VerificationType.forTag(tag);
        Logger.getLogger(getClass()).debug("tag " + tag + " (" + verificationType + ")");
        if (verificationType != null) {
            result = verificationType.create(constantPool, in);
        } else {
            String message = "Unknown verification type info tag '" + tag + "'";
            Logger.getLogger(AttributeFactory.class).warn(message);
            throw new IOException(message);
        }

        return result;
    }
}
