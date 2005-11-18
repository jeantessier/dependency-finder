package com.jeantessier.dependency;

import java.util.*;

import junit.framework.*;

public class TestCycleDetector extends TestCase {
    private NodeFactory factory;

    private Node a_package;
    private Node a_A_a_feature;

    private Node b_package;
    private Node b_B_b_feature;

    private Node c_package;
    private Node d_package;
    private Node e_package;

    private CycleDetector detector;

    protected void setUp() throws Exception {
        factory = new NodeFactory();

        a_package = factory.createPackage("a");
        a_A_a_feature = factory.createFeature("a.A.a");

        b_package = factory.createPackage("b");
        b_B_b_feature = factory.createFeature("b.B.b");

        c_package = factory.createPackage("c");
        d_package = factory.createPackage("d");
        e_package = factory.createPackage("e");

        detector = new CycleDetector();
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

    public void testOneLength2PackageCycle() {
        a_package.addDependency(b_package);
        b_package.addDependency(a_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 1, detector.getCycles().size());

        Iterator cycles = detector.getCycles().iterator();

        Cycle cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 2, cycle.getLength());
        Iterator i = cycle.getPath().iterator();
        assertEquals("a", a_package, i.next());
        assertEquals("b", b_package, i.next());
    }

    public void testOneLength3PackageCycle() {
        a_package.addDependency(b_package);
        b_package.addDependency(c_package);
        c_package.addDependency(a_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 1, detector.getCycles().size());

        Iterator cycles = detector.getCycles().iterator();

        Cycle cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 3, cycle.getLength());
        Iterator i = cycle.getPath().iterator();
        assertEquals("a", a_package, i.next());
        assertEquals("b", b_package, i.next());
        assertEquals("c", c_package, i.next());
    }

    public void testTwoLength3PackageCycles() {
        a_package.addDependency(b_package);
        b_package.addDependency(c_package);
        c_package.addDependency(a_package);
        c_package.addDependency(d_package);
        d_package.addDependency(e_package);
        e_package.addDependency(c_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 2, detector.getCycles().size());

        Iterator cycles = detector.getCycles().iterator();

        Cycle cycle;
        Iterator i;

        cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 3, cycle.getLength());
        i = cycle.getPath().iterator();
        assertEquals("a", a_package, i.next());
        assertEquals("b", b_package, i.next());
        assertEquals("c", c_package, i.next());

        cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 3, cycle.getLength());
        i = cycle.getPath().iterator();
        assertEquals("c", c_package, i.next());
        assertEquals("d", d_package, i.next());
        assertEquals("e", e_package, i.next());
    }

    public void testOneLength2AndOneLength3PackageCycles() {
        a_package.addDependency(b_package);
        b_package.addDependency(a_package);
        c_package.addDependency(d_package);
        d_package.addDependency(e_package);
        e_package.addDependency(c_package);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 2, detector.getCycles().size());

        Iterator cycles = detector.getCycles().iterator();

        Cycle cycle;
        Iterator i;

        cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 2, cycle.getLength());
        i = cycle.getPath().iterator();
        assertEquals("a", a_package, i.next());
        assertEquals("b", b_package, i.next());

        cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 3, cycle.getLength());
        i = cycle.getPath().iterator();
        assertEquals("c", c_package, i.next());
        assertEquals("d", d_package, i.next());
        assertEquals("e", e_package, i.next());
    }

    public void testMaximumLength() {
        a_package.addDependency(b_package);
        b_package.addDependency(a_package);
        c_package.addDependency(d_package);
        d_package.addDependency(e_package);
        e_package.addDependency(c_package);
        detector.setMaximumCycleLength(2);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 1, detector.getCycles().size());

        Iterator cycles = detector.getCycles().iterator();

        Cycle cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 2, cycle.getLength());
        Iterator i = cycle.getPath().iterator();
        assertEquals("a", a_package, i.next());
        assertEquals("b", b_package, i.next());
    }

    public void testOneLength2FeatureCycle() {
        a_A_a_feature.addDependency(b_B_b_feature);
        b_B_b_feature.addDependency(a_A_a_feature);
        detector.traverseNodes(factory.getPackages().values());
        assertEquals("Nb cycles", 1, detector.getCycles().size());

        Iterator cycles = detector.getCycles().iterator();

        Cycle cycle = (Cycle) cycles.next();
        assertEquals("cycle length", 2, cycle.getLength());
        Iterator i = cycle.getPath().iterator();
        assertEquals("a.A.a", a_A_a_feature, i.next());
        assertEquals("b.B.b", b_B_b_feature, i.next());
    }
}
