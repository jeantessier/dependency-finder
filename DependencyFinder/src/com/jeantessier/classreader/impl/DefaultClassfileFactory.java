package com.jeantessier.classreader.impl;

import java.io.*;

import com.jeantessier.classreader.*;

public class DefaultClassfileFactory implements ClassfileFactory {
    public Classfile create(ClassfileLoader loader, DataInputStream in) throws IOException {
        return new Classfile(loader, in);
    }
}
