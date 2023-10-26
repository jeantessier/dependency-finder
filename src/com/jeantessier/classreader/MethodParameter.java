package com.jeantessier.classreader;

public interface MethodParameter extends Visitable {
    public int getNameIndex();
    public UTF8_info getRawName();
    public String getName();

    public int getAccessFlags();
    public boolean isFinal();
    public boolean isSynthetic();
    public boolean isMandated();
}
