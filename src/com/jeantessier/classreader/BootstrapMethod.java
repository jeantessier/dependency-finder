package com.jeantessier.classreader;

import java.util.*;

public interface BootstrapMethod extends Visitable {
    public MethodHandle_info getBootstrapMethod();
    public Collection<ConstantPoolEntry> getArguments();
}
