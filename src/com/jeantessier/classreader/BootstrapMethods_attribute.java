package com.jeantessier.classreader;

import java.util.*;

public interface BootstrapMethods_attribute extends Attribute_info {
    public Collection<? extends BootstrapMethod> getBootstrapMethods();
}
