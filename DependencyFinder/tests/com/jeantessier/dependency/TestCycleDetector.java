package com.jeantessier.dependency;

import junit.framework.*;
import org.apache.log4j.*;

/**
 * TODO Class comment
 */
public class TestCycleDetector extends TestCase {
    private NodeFactory                        factory;

    private Node a_package;
    private Node b_package;
    private Node c_package;
    private Node d_package;
    private Node e_package;

    private CycleDetector detector;

    protected void setUp() throws Exception {
        Logger.getLogger(getClass()).info("Starting test: " + getName());

        factory        = new NodeFactory();

        a_package = factory.createPackage("a");
        b_package = factory.createPackage("b");
        c_package = factory.createPackage("c");
        d_package = factory.createPackage("d");
        e_package = factory.createPackage("e");

        detector = new CycleDetector();
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).info("End of " + getName());
    }

    public void testNoDependencies() {
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 0, detector.getCycles().size());
    }

    public void testNoCycles() {
        a_package.addDependency(b_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 0, detector.getCycles().size());
    }

    public void testOneLength2Cycle() {
        a_package.addDependency(b_package);
        b_package.addDependency(a_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 1, detector.getCycles().size());
    }

    public void testOneLength3Cycle() {
        a_package.addDependency(b_package);
        b_package.addDependency(c_package);
        c_package.addDependency(a_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 1, detector.getCycles().size());
    }

    public void testTwoLength3Cycles() {
        a_package.addDependency(b_package);
        b_package.addDependency(c_package);
        c_package.addDependency(a_package);
        c_package.addDependency(d_package);
        d_package.addDependency(e_package);
        e_package.addDependency(c_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 2, detector.getCycles().size());
    }
}
