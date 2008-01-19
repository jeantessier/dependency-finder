package com.jeantessier.classreader;

public interface LocalVariableType extends Visitable {
    public LocalVariableTypeTable_attribute getLocalVariableTypeTable() ;

    public int getStartPC();
    public int getLength();

    public int getNameIndex();
    public UTF8_info getRawName();
    public String getName();

    public int getSignatureIndex();
    public UTF8_info getRawSignature();
    public String getSignature();

    public int getIndex();
}
