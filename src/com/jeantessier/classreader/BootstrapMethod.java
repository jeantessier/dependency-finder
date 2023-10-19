package com.jeantessier.classreader;

import java.util.*;

public interface BootstrapMethod extends Visitable {
    public Collection<ConstantPoolEntry> getArguments();
}
