package com.jeantessier.classreader;

import java.util.*;

public interface StackMapTable_attribute extends Attribute_info {
    public Collection<? extends StackMapFrame> getEntries();
}
