package com.jeantessier.classreader.impl;

import java.io.*;

import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;
import org.jmock.*;

import com.jeantessier.classreader.*;

/**
 * Created by IntelliJ IDEA. User: jeantessier Date: Oct 28, 2008 Time: 11:21:18
 * PM To change this template use File | Settings | File Templates.
 */
public class TestAttributeBase extends MockObjectTestCase {
    protected Classfile mockClassfile;

    protected Visitable mockOwner;

    protected DataInput mockIn;

    protected Sequence dataReads;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockClassfile = mock(Classfile.class);
        mockOwner = mock(Visitable.class);
        mockIn = mock(DataInput.class);

        dataReads = sequence("dataReads");

    }

    protected void expectAttributeLength(final int length) throws IOException {
        checking(new Expectations() {{
            one (mockIn).readInt();
                inSequence(dataReads);
                will(returnValue(length));
        }});
    }
}
