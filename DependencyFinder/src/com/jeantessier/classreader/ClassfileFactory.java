package com.jeantessier.classreader;

import java.io.*;

public interface ClassfileFactory {
    public Classfile create(ClassfileLoader loader, DataInputStream in) throws IOException;
}
