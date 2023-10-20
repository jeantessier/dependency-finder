package com.jeantessier.classreader;

import java.util.*;

public interface BootstrapMethod extends Visitable {
    public int getBootstrapMethodRef();
    public MethodHandle_info getBootstrapMethod();
    public Collection<Integer> getArgumentIndices();
    public ConstantPoolEntry getArgument(int index);
    public Collection<ConstantPoolEntry> getArguments();
}
