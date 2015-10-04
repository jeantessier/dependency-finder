/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.dependency;

import junit.framework.*;

import org.apache.log4j.*;

public class TestDeletingVisitor extends TestCase {
    private NodeFactory     factory;
    private DeletingVisitor visitor;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).debug("Begin " + getName());
        
        factory = new NodeFactory();
        visitor = new DeletingVisitor(factory);
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).debug("End " + getName());

        super.tearDown();
    }

    public void testCreation() {
        assertSame("factory", factory, visitor.getFactory());
    }
    
    /**
     *  <p>Deleting package really deletes it.</p>
     *  
     *  <pre>
     *  a          <-- delete this
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptEmptyPackage() {
        PackageNode node = factory.createPackage("a", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node));
        assertTrue("Package not concrete", node.isConfirmed());

        node.accept(visitor);
        
        assertFalse("Did not remove package key", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value", factory.getPackages().containsValue(node));
        assertFalse("Package is still concrete", node.isConfirmed());
    }

    /**
     *  <p>Deleting package deletes its classes.</p>
     *  
     *  <pre>
     *  a          <-- delete this
     *  +-- A
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptPackageWithClasses() {
        ClassNode node = factory.createClass("a.A", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node.getPackageNode()));
        assertTrue("Package not concrete", node.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getPackageNode().getClasses().contains(node));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node));
        assertTrue("Class not concrete", node.isConfirmed());

        node.getPackageNode().accept(visitor);
        
        assertFalse("Did not remove package key", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value", factory.getPackages().containsValue(node.getPackageNode()));
        assertFalse("Package is still concrete", node.getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node.getPackageNode().getClasses().contains(node));

        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node));
        assertFalse("Class is still concrete", node.isConfirmed());
    }

    /**
     *  <p>Deleting class preserves package and siblings.</p>
     *  
     *  <pre>
     *  a
     *  +-- A      <-- delete this
     *  +-- B
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  a
     *  +-- B
     *  </pre>
     */
    public void testAcceptEmptyClass() {
        ClassNode node1 = factory.createClass("a.A", true);
        ClassNode node2 = factory.createClass("a.B", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package not concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node does not contain class node", node2.getPackageNode().getClasses().contains(node2));
        assertSame("Classes have different package", node1.getPackageNode(), node2.getPackageNode());
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1));
        assertTrue("Class not concrete", node1.isConfirmed());

        node1.accept(visitor);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package not concrete", node1.getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node does not contain class node", node2.getPackageNode().getClasses().contains(node2));
        assertSame("Classes have different package", node1.getPackageNode(), node2.getPackageNode());

        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node1));
        assertFalse("Class is still concrete", node1.isConfirmed());
    }

    /**
     *  <p>Deleting single class deletes its package.</p>
     *  
     *  <pre>
     *  a
     *  +-- A      <-- delete this
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptSingleEmptyClass() {
        ClassNode node = factory.createClass("a.A", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node.getPackageNode()));
        assertTrue("Package not concrete", node.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getPackageNode().getClasses().contains(node));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node));
        assertTrue("Class not concrete", node.isConfirmed());

        node.accept(visitor);
        
        assertFalse("Did not remove package key", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value", factory.getPackages().containsValue(node.getPackageNode()));
        assertFalse("Package is still concrete", node.getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node.getPackageNode().getClasses().contains(node));

        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node));
        assertFalse("Class is still concrete", node.isConfirmed());
    }

    /**
     *  <p>Deleting class deletes its features and preserves package and siblings.</p>
     *  
     *  <pre>
     *  a
     *  +-- A      <-- delete this
     *  |   +-- a
     *  +-- B
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  a
     *  +-- B
     *  </pre>
     */
    public void testAcceptClassWithFeature() {
        FeatureNode node1 = factory.createFeature("a.A.a", true);
        ClassNode   node2 = factory.createClass("a.B", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        assertTrue("Package node does not contain class node", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class not concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node", node1.getClassNode().getFeatures().contains(node1));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature not concrete", node1.isConfirmed());

        node1.getClassNode().accept(visitor);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node1.getClassNode().getPackageNode().getClasses().contains(node1));
        assertTrue("Package node does not contain class node", node2.getPackageNode().getClasses().contains(node2));

        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertFalse("Class is still concrete", node1.getClassNode().isConfirmed());
        assertFalse("Class node still contains feature node", node1.getClassNode().getFeatures().contains(node1));

        assertFalse("Did not remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value", factory.getFeatures().containsValue(node1));
        assertFalse("Feature is still concrete", node1.isConfirmed());
    }

    /**
     *  <p>Deleting single class deletes its features and its package.</p>
     *  
     *  <pre>
     *  a
     *  +-- A      <-- delete this
     *      +-- a
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptSingleClassWithFeature() {
        FeatureNode node1 = factory.createFeature("a.A.a", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class not concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node", node1.getClassNode().getFeatures().contains(node1));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature not concrete", node1.isConfirmed());

        node1.getClassNode().accept(visitor);
        
        assertFalse("Did not remove package key", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertFalse("Package is still concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node1.getClassNode().getPackageNode().getClasses().contains(node1));

        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertFalse("Class is still concrete", node1.getClassNode().isConfirmed());
        assertFalse("Class node still contains feature node", node1.getClassNode().getFeatures().contains(node1));

        assertFalse("Did not remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value", factory.getFeatures().containsValue(node1));
        assertFalse("Feature is still concrete", node1.isConfirmed());
    }

    /**
     *  <p>Deleting feature preserves class and siblings.</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *      +-- a  <-- delete this
     *      +-- b
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *      +-- b
     *  </pre>
     */
    public void testAcceptEmptyFeature() {
        FeatureNode node1 = factory.createFeature("a.A.a", true);
        FeatureNode node2 = factory.createFeature("a.A.b", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class not concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node does not contain feature node", node2.getClassNode().getFeatures().contains(node2));
        assertSame("Features have different class", node1.getClassNode(), node2.getClassNode());
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature not concrete", node1.isConfirmed());

        node1.accept(visitor);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class not concrete", node1.getClassNode().isConfirmed());
        assertFalse("Class node still contains feature node", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node does not contain feature node", node2.getClassNode().getFeatures().contains(node2));
        assertSame("Features have different class", node1.getClassNode(), node2.getClassNode());

        assertFalse("Did not remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value", factory.getFeatures().containsValue(node1));
        assertFalse("Feature is still concrete", node1.isConfirmed());
    }

    /**
     *  <p>Deleting single feature of single class deletes entire package.</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *      +-- a  <-- delete this
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *  </pre>
     */
    public void testAcceptSingleEmptyFeature() {
        FeatureNode node = factory.createFeature("a.A.a", true);
        
        assertTrue("Missing package key", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value", factory.getPackages().containsValue(node.getClassNode().getPackageNode()));
        assertTrue("Package not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node.getClassNode()));
        assertTrue("Class not concrete", node.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node", node.getClassNode().getFeatures().contains(node));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node));
        assertTrue("Feature not concrete", node.isConfirmed());

        node.accept(visitor);
        
        assertTrue("Removed package key", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value", factory.getPackages().containsValue(node.getClassNode().getPackageNode()));
        assertTrue("Package is no longer concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()));

        assertTrue("Removed class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value", factory.getClasses().containsValue(node.getClassNode()));
        assertTrue("Class is no longer concrete", node.getClassNode().isConfirmed());
        assertFalse("Class node still contains feature node", node.getClassNode().getFeatures().contains(node));

        assertFalse("Did not remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value", factory.getFeatures().containsValue(node));
        assertFalse("Feature is still concrete", node.isConfirmed());
    }

    /**
     *  <p>Deleting package leaves other concrete packages intact.</p>
     *  
     *  <pre>
     *  a          <-- delete this
     *      <-- b
     *  b
     *      --> a
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  b
     *  </pre>
     */
    public void testAcceptPackageWithDependencyOnConcretePackage() {
        PackageNode a = factory.createPackage("a", true);
        PackageNode b = factory.createPackage("b", true);

        a.addDependency(b);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a));
        assertTrue("Package a not concrete", a.isConfirmed());
        assertTrue("a --> b is missing", a.getOutboundDependencies().contains(b));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b));
        assertTrue("Package b not concrete", b.isConfirmed());
        assertTrue("b <-- a is missing", b.getInboundDependencies().contains(a));

        a.accept(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(a));
        assertFalse("Package a is still concrete", a.isConfirmed());
        assertFalse("Did not remove a --> b", a.getOutboundDependencies().contains(b));
        
        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(b));
        assertTrue("Package b is no longer concrete", b.isConfirmed());
        assertFalse("Did not remove b <-- a", b.getInboundDependencies().contains(a));
    }

    /**
     *  <p>Deleting package removes obsolete non-concrete packages.</p>
     *  
     *  <pre>
     *  a          <-- delete this
     *      <-- b
     *  b
     *      --> a
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptPackageWithDependencyOnNonConcretePackage() {
        PackageNode a = factory.createPackage("a", true);
        PackageNode b = factory.createPackage("b", false);

        a.addDependency(b);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a));
        assertTrue("Package a not concrete", a.isConfirmed());
        assertTrue("a --> b is missing", a.getOutboundDependencies().contains(b));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b));
        assertFalse("Package b is concrete", b.isConfirmed());
        assertTrue("b <-- a is missing", b.getInboundDependencies().contains(a));

        a.accept(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(a));
        assertFalse("Package a is still concrete", a.isConfirmed());
        assertFalse("Did not remove a --> b", a.getOutboundDependencies().contains(b));
        
        assertFalse("Did not remove package key b", factory.getPackages().containsKey("b"));
        assertFalse("Did not remove package value b", factory.getPackages().containsValue(b));
        assertFalse("Package b became concrete", b.isConfirmed());
        assertFalse("Did not remove b <-- a", b.getInboundDependencies().contains(a));
    }

    /**
     *  <p>Deleting class leaves other concrete classes intact.</p>
     *  
     *  <pre>
     *  a
     *  +-- A          <-- delete this
     *          <-- b.B
     *  b
     *  +-- B
     *          --> a.A
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  b
     *  +-- B
     *  </pre>
     */
    public void testAcceptClassWithDependencyOnConcreteClass() {
        ClassNode a_A = factory.createClass("a.A", true);
        ClassNode b_B = factory.createClass("b.B", true);

        a_A.addDependency(b_B);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a_A.getPackageNode()));
        assertTrue("Package a not concrete", a_A.getPackageNode().isConfirmed());
        assertTrue("Missing class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value a.A", factory.getClasses().containsValue(a_A));
        assertTrue("Class a.A not concrete", a_A.isConfirmed());
        assertTrue("a.A --> b.B is missing", a_A.getOutboundDependencies().contains(b_B));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b_B.getPackageNode()));
        assertTrue("Package b not concrete", b_B.getPackageNode().isConfirmed());
        assertTrue("Missing class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Missing class value b.B", factory.getClasses().containsValue(b_B));
        assertTrue("Class b.B not concrete", b_B.isConfirmed());
        assertTrue("b.B <-- a.A is missing", b_B.getInboundDependencies().contains(a_A));

        a_A.accept(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(a_A.getPackageNode()));
        assertFalse("Package a is still concrete", a_A.getPackageNode().isConfirmed());
        assertFalse("Did not remove class key a.A", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value a.A", factory.getClasses().containsValue(a_A));
        assertFalse("Class a.A is still concrete", a_A.isConfirmed());
        assertFalse("Did not remove a.A --> b.B", a_A.getOutboundDependencies().contains(b_B));
        
        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(b_B.getPackageNode()));
        assertTrue("Package b is no longer concrete", b_B.isConfirmed());
        assertTrue("Removed class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Removed class value b.B", factory.getClasses().containsValue(b_B));
        assertTrue("Class b.B is no longer concrete", b_B.isConfirmed());
        assertFalse("Did not remove b.B <-- a.A", b_B.getInboundDependencies().contains(a_A));
    }

    /**
     *  <p>Deleting class removes other obsolete classes.</p>
     *  
     *  <pre>
     *  a
     *  +-- A          <-- delete this
     *          <-- b.B
     *  b
     *  +-- B
     *          --> a.A
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptClassWithDependencyOnReferencedClass() {
        ClassNode a_A = factory.createClass("a.A", true);
        ClassNode b_B = factory.createClass("b.B", false);

        a_A.addDependency(b_B);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a_A.getPackageNode()));
        assertTrue("Package a not concrete", a_A.getPackageNode().isConfirmed());
        assertTrue("Missing class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value a.A", factory.getClasses().containsValue(a_A));
        assertTrue("Class a.A not concrete", a_A.isConfirmed());
        assertTrue("a.A --> b.B is missing", a_A.getOutboundDependencies().contains(b_B));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b_B.getPackageNode()));
        assertFalse("Package b is concrete", b_B.getPackageNode().isConfirmed());
        assertTrue("Missing class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Missing class value b.B", factory.getClasses().containsValue(b_B));
        assertFalse("Class b.B is concrete", b_B.isConfirmed());
        assertTrue("b.B <-- a.A is missing", b_B.getInboundDependencies().contains(a_A));

        a_A.accept(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(a_A.getPackageNode()));
        assertFalse("Package a is still concrete", a_A.getPackageNode().isConfirmed());
        assertFalse("Did not remove class key a.A", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value a.A", factory.getClasses().containsValue(a_A));
        assertFalse("Class a.A is still concrete", a_A.isConfirmed());
        assertFalse("Did not remove a.A --> b.B", a_A.getOutboundDependencies().contains(b_B));
        
        assertFalse("Did not remove package key b", factory.getPackages().containsKey("b"));
        assertFalse("Did not remove package value b", factory.getPackages().containsValue(b_B.getPackageNode()));
        assertFalse("Package b is now concrete", b_B.getPackageNode().isConfirmed());
        assertFalse("Did not remove class key b.B", factory.getClasses().containsKey("b.B"));
        assertFalse("Did not remove class value b.B", factory.getClasses().containsValue(b_B));
        assertFalse("Class b.B is now concrete", b_B.isConfirmed());
        assertFalse("Did not remove b.B <-- a.A", b_B.getInboundDependencies().contains(a_A));
    }

    /**
     *  <p>Deleting class leaves other concrete features intact.</p>
     *  
     *  <pre>
     *  a
     *  +-- A           <-- delete this
     *      +-- a
     *              <-- b.B.b
     *  b
     *  +-- B
     *      +-- b
     *              --> a.A.a
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  b
     *  +-- B
     *      +-- b
     *  </pre>
     */
    public void testAcceptClassWithFeatureWithDependencyOnConcreteFeature() {
        FeatureNode a_A_a = factory.createFeature("a.A.a", true);
        FeatureNode b_B_b = factory.createFeature("b.B.b", true);

        a_A_a.addDependency(b_B_b);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertTrue("Package a not concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertTrue("Class a.A not concrete", a_A_a.getClassNode().isConfirmed());
        assertTrue("Missing feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertTrue("Feature a.A.a not concrete", a_A_a.isConfirmed());
        assertTrue("a.A.a --> b.B.b is missing", a_A_a.getOutboundDependencies().contains(b_B_b));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertTrue("Package b not concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Missing class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertTrue("Class b.B not concrete", b_B_b.getClassNode().isConfirmed());
        assertTrue("Missing feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertTrue("Missing feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertTrue("Feature b.B.b not concrete", b_B_b.isConfirmed());
        assertTrue("b.B <-- a.A is missing", b_B_b.getInboundDependencies().contains(a_A_a));

        a_A_a.getClassNode().accept(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertFalse("Package a is still concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Did not remove class key a.A", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertFalse("Class a.A is still concrete", a_A_a.getClassNode().isConfirmed());
        assertFalse("Did not remove feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertFalse("Feature a.A.a is still concrete", a_A_a.isConfirmed());
        assertFalse("Did not remove a.A --> b.B", a_A_a.getOutboundDependencies().contains(b_B_b));
        
        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertTrue("Package b is no longer concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Removed class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Removed class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertTrue("Class b.B is no longer concrete", b_B_b.getClassNode().isConfirmed());
        assertTrue("Removed feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertTrue("Removed feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertTrue("Feature b.B.b is no longer concrete", b_B_b.isConfirmed());
        assertFalse("Did not remove b.B <-- a.A", b_B_b.getInboundDependencies().contains(a_A_a));
    }

    /**
     *  <p>Deleting feature leaves other concrete features intact.</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *      +-- a       <-- delete this
     *              <-- b.B.b
     *  b
     *  +-- B
     *      +-- b
     *              --> a.A.a
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *  
     *  b
     *  +-- B
     *      +-- b
     *  </pre>
     */
    public void testAcceptFeatureWithDependencyOnConcreteFeature() {
        FeatureNode a_A_a = factory.createFeature("a.A.a", true);
        FeatureNode b_B_b = factory.createFeature("b.B.b", true);

        a_A_a.addDependency(b_B_b);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertTrue("Package a not concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertTrue("Class a.A not concrete", a_A_a.getClassNode().isConfirmed());
        assertTrue("Missing feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertTrue("Feature a.A.a not concrete", a_A_a.isConfirmed());
        assertTrue("a.A.a --> b.B.b is missing", a_A_a.getOutboundDependencies().contains(b_B_b));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertTrue("Package b not concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Missing class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertTrue("Class b.B not concrete", b_B_b.getClassNode().isConfirmed());
        assertTrue("Missing feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertTrue("Missing feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertTrue("Feature b.B.b not concrete", b_B_b.isConfirmed());
        assertTrue("b.B <-- a.A is missing", b_B_b.getInboundDependencies().contains(a_A_a));

        a_A_a.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertTrue("Package a is no longer concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Removed class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertTrue("Class a.A is no longer concrete", a_A_a.getClassNode().isConfirmed());
        assertFalse("Did not remove feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertFalse("Feature a.A.a is still concrete", a_A_a.isConfirmed());
        assertFalse("Did not remove a.A --> b.B", a_A_a.getOutboundDependencies().contains(b_B_b));
        
        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertTrue("Package b is no longer concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Removed class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Removed class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertTrue("Class b.B is no longer concrete", b_B_b.getClassNode().isConfirmed());
        assertTrue("Removed feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertTrue("Removed feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertTrue("Feature b.B.b is no longer concrete", b_B_b.isConfirmed());
        assertFalse("Did not remove b.B <-- a.A", b_B_b.getInboundDependencies().contains(a_A_a));
    }

    /**
     *  <p>Deleting class removes obsolete features.</p>
     *  
     *  <pre>
     *  a
     *  +-- A           <-- delete this
     *      +-- a
     *              <-- b.B.b
     *  b
     *  +-- B
     *      +-- b
     *              --> a.A.a
     *  </pre>
     *
     *  <p>becomes:</p>
     */
    public void testAcceptClassWithFeatureWithDependencyOnReferencedFeature() {
        FeatureNode a_A_a = factory.createFeature("a.A.a", true);
        FeatureNode b_B_b = factory.createFeature("b.B.b", false);

        a_A_a.addDependency(b_B_b);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertTrue("Package a not concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertTrue("Class a.A not concrete", a_A_a.getClassNode().isConfirmed());
        assertTrue("Missing feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertTrue("Feature a.A.a not concrete", a_A_a.isConfirmed());
        assertTrue("a.A.a --> b.B.b is missing", a_A_a.getOutboundDependencies().contains(b_B_b));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertFalse("Package b is concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Missing class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertFalse("Class b.B is concrete", b_B_b.getClassNode().isConfirmed());
        assertTrue("Missing feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertTrue("Missing feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertFalse("Feature b.B.b is concrete", b_B_b.isConfirmed());
        assertTrue("b.B <-- a.A is missing", b_B_b.getInboundDependencies().contains(a_A_a));

        a_A_a.getClassNode().accept(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertFalse("Package a is still concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Did not remove class key a.A", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertFalse("Class a.A is still concrete", a_A_a.getClassNode().isConfirmed());
        assertFalse("Did not remove feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertFalse("Feature a.A.a is still concrete", a_A_a.isConfirmed());
        assertFalse("Did not remove a.A --> b.B", a_A_a.getOutboundDependencies().contains(b_B_b));
        
        assertFalse("Did not remove package key b", factory.getPackages().containsKey("b"));
        assertFalse("Did not remove package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertFalse("Package b is now concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Did not remove class key b.B", factory.getClasses().containsKey("b.B"));
        assertFalse("Did not remove class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertFalse("Class b.B is now concrete", b_B_b.getClassNode().isConfirmed());
        assertFalse("Did not remove feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertFalse("Did not remove feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertFalse("Feature b.B.b is now concrete", b_B_b.isConfirmed());
        assertFalse("Did not remove b.B <-- a.A", b_B_b.getInboundDependencies().contains(a_A_a));
    }

    /**
     *  <p>Deleting feature removes obsolete features.</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *      +-- a       <-- delete this
     *              <-- b.B.b
     *  b
     *  +-- B
     *      +-- b
     *              --> a.A.a
     *  </pre>
     *
     *  <p>becomes:</p>
     *  
     *  <pre>
     *  a
     *  +-- A
     *  </pre>
     */
    public void testAcceptFeatureWithDependencyOnReferencedFeature() {
        FeatureNode a_A_a = factory.createFeature("a.A.a", true);
        FeatureNode b_B_b = factory.createFeature("b.B.b", false);

        a_A_a.addDependency(b_B_b);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertTrue("Package a not concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertTrue("Class a.A not concrete", a_A_a.getClassNode().isConfirmed());
        assertTrue("Missing feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertTrue("Feature a.A.a not concrete", a_A_a.isConfirmed());
        assertTrue("a.A.a --> b.B.b is missing", a_A_a.getOutboundDependencies().contains(b_B_b));

        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertFalse("Package b is concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Missing class key b.B", factory.getClasses().containsKey("b.B"));
        assertTrue("Missing class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertFalse("Class b.B is concrete", b_B_b.getClassNode().isConfirmed());
        assertTrue("Missing feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertTrue("Missing feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertFalse("Feature b.B.b is concrete", b_B_b.isConfirmed());
        assertTrue("b.B <-- a.A is missing", b_B_b.getInboundDependencies().contains(a_A_a));

        a_A_a.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()));
        assertTrue("Package a is no longer concrete", a_A_a.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Removed class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value a.A", factory.getClasses().containsValue(a_A_a.getClassNode()));
        assertTrue("Class a.A is no longer concrete", a_A_a.getClassNode().isConfirmed());
        assertFalse("Did not remove feature key a.A.a", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value a.A.a", factory.getFeatures().containsValue(a_A_a));
        assertFalse("Feature a.A.a is still concrete", a_A_a.isConfirmed());
        assertFalse("Did not remove a.A --> b.B", a_A_a.getOutboundDependencies().contains(b_B_b));
        
        assertFalse("Did not remove package key b", factory.getPackages().containsKey("b"));
        assertFalse("Did not remove package value b", factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()));
        assertFalse("Package b is now concrete", b_B_b.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Did not remove class key b.B", factory.getClasses().containsKey("b.B"));
        assertFalse("Did not remove class value b.B", factory.getClasses().containsValue(b_B_b.getClassNode()));
        assertFalse("Class b.B is now concrete", b_B_b.getClassNode().isConfirmed());
        assertFalse("Did not remove feature key b.B.b", factory.getFeatures().containsKey("b.B.b"));
        assertFalse("Did not remove feature value b.B.b", factory.getFeatures().containsValue(b_B_b));
        assertFalse("Feature b.B.b is now concrete", b_B_b.isConfirmed());
        assertFalse("Did not remove b.B <-- a.A", b_B_b.getInboundDependencies().contains(a_A_a));
    }

    public void testAcceptOutboundConcretePackage() {
        PackageNode node = factory.createPackage("a", true);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node));
        assertTrue("Package a not concrete", node.isConfirmed());

        node.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node));
        assertTrue("Package a is no longer concrete", node.isConfirmed());
    }

    public void testAcceptOutboundConcreteClass() {
        ClassNode node = factory.createClass("a.A", true);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node.getPackageNode()));
        assertTrue("Package a not concrete", node.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getPackageNode().getClasses().contains(node));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node));
        assertTrue("Class not concrete", node.isConfirmed());

        node.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node.getPackageNode()));
        assertTrue("Package a is no longer concrete", node.getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node.getPackageNode().getClasses().contains(node));
        
        assertTrue("Removed class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value", factory.getClasses().containsValue(node));
        assertTrue("Class a.A is no longer concrete", node.isConfirmed());
    }

    public void testAcceptOutboundConcreteFeature() {
        FeatureNode node = factory.createFeature("a.A.a", true);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node.getClassNode().getPackageNode()));
        assertTrue("Package a not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node.getClassNode()));
        assertTrue("Class not concrete", node.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node", node.getClassNode().getFeatures().contains(node));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node));
        assertTrue("Feature not concrete", node.isConfirmed());

        node.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node.getClassNode().getPackageNode()));
        assertTrue("Package a is no longer concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()));
        
        assertTrue("Removed class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value", factory.getClasses().containsValue(node.getClassNode()));
        assertTrue("Class a.A is no longer concrete", node.getClassNode().isConfirmed());
        assertTrue("Class node no longer contains feature node", node.getClassNode().getFeatures().contains(node));
        
        assertTrue("Removed feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Removed feature value", factory.getFeatures().containsValue(node));
        assertTrue("Feature a.A.a is no longer concrete", node.isConfirmed());
    }

    public void testAcceptOutboundEmptyNonConcretePackage() {
        PackageNode node = factory.createPackage("a", false);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node));
        assertFalse("Package a concrete", node.isConfirmed());

        node.acceptOutbound(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(node));
        assertFalse("Package a is now concrete", node.isConfirmed());
    }

    public void testAcceptOutboundEmptyNonConcreteClass() {
        ClassNode node = factory.createClass("a.A", false);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node.getPackageNode()));
        assertFalse("Package a is concrete", node.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getPackageNode().getClasses().contains(node));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node));
        assertFalse("Class a.A is concrete", node.isConfirmed());

        node.acceptOutbound(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(node.getPackageNode()));
        assertFalse("Package a is now concrete", node.getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node.getPackageNode().getClasses().contains(node));
        
        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node));
        assertFalse("Class a.A is now concrete", node.isConfirmed());
    }

    public void testAcceptOutboundEmptyNonConcreteFeature() {
        FeatureNode node = factory.createFeature("a.A.a", false);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node.getClassNode().getPackageNode()));
        assertFalse("Package a is concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node.getClassNode()));
        assertFalse("Class a.A is concrete", node.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node", node.getClassNode().getFeatures().contains(node));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node));
        assertFalse("Feature a.A.a is concrete", node.isConfirmed());

        node.acceptOutbound(visitor);
        
        assertFalse("Did not remove package key a", factory.getPackages().containsKey("a"));
        assertFalse("Did not remove package value a", factory.getPackages().containsValue(node.getClassNode().getPackageNode()));
        assertFalse("Package a is now concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Package node still contains class node", node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()));
        
        assertFalse("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertFalse("Did not remove class value", factory.getClasses().containsValue(node.getClassNode()));
        assertFalse("Class a.A is now concrete", node.getClassNode().isConfirmed());
        assertFalse("Class node still contains feature node", node.getClassNode().getFeatures().contains(node));
        
        assertFalse("Did not remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertFalse("Did not remove feature value", factory.getFeatures().containsValue(node));
        assertFalse("Feature a.A.a is now concrete", node.isConfirmed());
    }

    public void testAcceptOutboundEmptyNonConcretePackageWithInboundDependency() {
        PackageNode node1 = factory.createPackage("a", true);
        PackageNode node2 = factory.createPackage("b", false);

        node1.addDependency(node2);
        
        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1));
        assertTrue("Package a is not concrete", node1.isConfirmed());
        assertTrue("a --> b is missing", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(node2));
        assertFalse("Package b is concrete", node2.isConfirmed());
        assertTrue("b <-- a is missing", node2.getInboundDependencies().contains(node1));

        node2.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1));
        assertTrue("Package a is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a --> b", node1.getOutboundDependencies().contains(node2));

        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(node2));
        assertFalse("Package b is now concrete", node2.isConfirmed());
        assertTrue("Removed b <-- a", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptOutboundEmptyNonConcreteClassWithInboundDependency() {
        ClassNode node1 = factory.createClass("a.A", true);
        ClassNode node2 = factory.createClass("a.B", false);

        node1.addDependency(node2);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package a is not concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node a.A", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node does not contain class node a.B", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1));
        assertTrue("Class a.A is not concrete", node1.isConfirmed());
        assertTrue("a.A --> a.B is missing", node1.getOutboundDependencies().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.B"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node2));
        assertFalse("Class a.B is concrete", node2.isConfirmed());
        assertTrue("a.B <-- a.A is missing", node2.getInboundDependencies().contains(node1));

        node2.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package a is no longer concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node a.A", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node no longer contains class node a.B", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Removed class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value a.A", factory.getClasses().containsValue(node1));
        assertTrue("Class a.A is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a.A --> a.B", node1.getOutboundDependencies().contains(node2));

        assertTrue("Removed class key a.B", factory.getClasses().containsKey("a.B"));
        assertTrue("Removed class value a.B", factory.getClasses().containsValue(node2));
        assertFalse("Class a.B is now concrete", node2.isConfirmed());
        assertTrue("Removed a.B <-- a.A", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptOutboundEmptyNonConcreteFeatureWithInboundDependency() {
        FeatureNode node1 = factory.createFeature("a.A.a", true);
        FeatureNode node2 = factory.createFeature("a.A.b", false);

        node1.addDependency(node2);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package a is not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class a.A is not concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node a.A.a", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node does not contain feature node a.A.b", node2.getClassNode().getFeatures().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature a.A.a is not concrete", node1.isConfirmed());
        assertTrue("a.A.a --> a.A.b is missing", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.b"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.b is concrete", node2.isConfirmed());
        assertTrue("a.A.b <-- a.A.a is missing", node2.getInboundDependencies().contains(node1));

        node2.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package a is no longer concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Did not remove class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class a.A is no longer concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node no longer contains feature node a.A.a", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node no longer contains feature node a.A.b", node2.getClassNode().getFeatures().contains(node2));
        
        assertTrue("Remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Remove feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature a.A.a is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a.A.a --> a.A.b", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Remove feature key", factory.getFeatures().containsKey("a.A.b"));
        assertTrue("Remove feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.b is now concrete", node2.isConfirmed());
        assertTrue("Removed a.A.b <-- a.A.a", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptEmptyNonConcretePackageWithInboundDependency() {
        PackageNode node1 = factory.createPackage("a", true);
        PackageNode node2 = factory.createPackage("b", false);

        node1.addDependency(node2);
        
        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1));
        assertTrue("Package a is not concrete", node1.isConfirmed());
        assertTrue("a --> b is missing", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(node2));
        assertFalse("Package b is concrete", node2.isConfirmed());
        assertTrue("b <-- a is missing", node2.getInboundDependencies().contains(node1));

        node2.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1));
        assertTrue("Package a is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a --> b", node1.getOutboundDependencies().contains(node2));

        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(node2));
        assertFalse("Package b is now concrete", node2.isConfirmed());
        assertTrue("Removed b <-- a", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptEmptyNonConcreteClassWithInboundDependency() {
        ClassNode node1 = factory.createClass("a.A", true);
        ClassNode node2 = factory.createClass("a.B", false);

        node1.addDependency(node2);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package a is not concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node a.A", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node does not contain class node a.B", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1));
        assertTrue("Class a.A is not concrete", node1.isConfirmed());
        assertTrue("a.A --> a.B is missing", node1.getOutboundDependencies().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.B"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node2));
        assertFalse("Class a.B is concrete", node2.isConfirmed());
        assertTrue("a.B <-- a.A is missing", node2.getInboundDependencies().contains(node1));

        node2.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package a is no longer concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node a.A", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node no longer contains class node a.B", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Removed class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value a.A", factory.getClasses().containsValue(node1));
        assertTrue("Class a.A is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a.A --> a.B", node1.getOutboundDependencies().contains(node2));

        assertTrue("Removed class key a.B", factory.getClasses().containsKey("a.B"));
        assertTrue("Removed class value a.B", factory.getClasses().containsValue(node2));
        assertFalse("Class a.B is now concrete", node2.isConfirmed());
        assertTrue("Removed a.B <-- a.A", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptEmptyNonConcreteFeatureWithInboundDependency() {
        FeatureNode node1 = factory.createFeature("a.A.a", true);
        FeatureNode node2 = factory.createFeature("a.A.b", false);

        node1.addDependency(node2);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package a is not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class a.A is not concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node a.A.a", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node does not contain feature node a.A.b", node2.getClassNode().getFeatures().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature a.A.a is not concrete", node1.isConfirmed());
        assertTrue("a.A.a --> a.A.b is missing", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.b"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.b is concrete", node2.isConfirmed());
        assertTrue("a.A.b <-- a.A.a is missing", node2.getInboundDependencies().contains(node1));

        node2.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package a is no longer concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Did not remove class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class a.A is no longer concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node no longer contains feature node a.A.a", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node no longer contains feature node a.A.b", node2.getClassNode().getFeatures().contains(node2));
        
        assertTrue("Remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Remove feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature a.A.a is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a.A.a --> a.A.b", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Remove feature key", factory.getFeatures().containsKey("a.A.b"));
        assertTrue("Remove feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.b is now concrete", node2.isConfirmed());
        assertTrue("Removed a.A.b <-- a.A.a", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptEmptyConcretePackageWithInboundDependency() {
        PackageNode node1 = factory.createPackage("a", true);
        PackageNode node2 = factory.createPackage("b", true);

        node1.addDependency(node2);
        
        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1));
        assertTrue("Package a is not concrete", node1.isConfirmed());
        assertTrue("a --> b is missing", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Missing package key b", factory.getPackages().containsKey("b"));
        assertTrue("Missing package value b", factory.getPackages().containsValue(node2));
        assertTrue("Package b is not concrete", node2.isConfirmed());
        assertTrue("b <-- a is missing", node2.getInboundDependencies().contains(node1));

        node2.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1));
        assertTrue("Package a is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a --> b", node1.getOutboundDependencies().contains(node2));

        assertTrue("Removed package key b", factory.getPackages().containsKey("b"));
        assertTrue("Removed package value b", factory.getPackages().containsValue(node2));
        assertFalse("Package b is still concrete", node2.isConfirmed());
        assertTrue("Removed b <-- a", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptEmptyConcreteClassWithInboundDependency() {
        ClassNode node1 = factory.createClass("a.A", true);
        ClassNode node2 = factory.createClass("a.B", true);

        node1.addDependency(node2);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package a is not concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node a.A", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node does not contain class node a.B", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1));
        assertTrue("Class a.A is not concrete", node1.isConfirmed());
        assertTrue("a.A --> a.B is missing", node1.getOutboundDependencies().contains(node2));

        assertTrue("Missing class key", factory.getClasses().containsKey("a.B"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node2));
        assertTrue("Class a.B is not concrete", node2.isConfirmed());
        assertTrue("a.B <-- a.A is missing", node2.getInboundDependencies().contains(node1));

        node2.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertTrue("Package a is no longer concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node a.A", node1.getPackageNode().getClasses().contains(node1));
        assertTrue("Package node no longer contains class node a.B", node2.getPackageNode().getClasses().contains(node2));

        assertTrue("Removed class key a.A", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value a.A", factory.getClasses().containsValue(node1));
        assertTrue("Class a.A is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a.A --> a.B", node1.getOutboundDependencies().contains(node2));

        assertTrue("Removed class key a.B", factory.getClasses().containsKey("a.B"));
        assertTrue("Removed class value a.B", factory.getClasses().containsValue(node2));
        assertFalse("Class a.B is still concrete", node2.isConfirmed());
        assertTrue("Removed a.B <-- a.A", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptEmptyConcreteFeatureWithInboundDependency() {
        FeatureNode node1 = factory.createFeature("a.A.a", true);
        FeatureNode node2 = factory.createFeature("a.A.b", true);

        node1.addDependency(node2);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package a is not concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class a.A is not concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node does not contain feature node a.A.a", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node does not contain feature node a.A.b", node2.getClassNode().getFeatures().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature a.A.a is not concrete", node1.isConfirmed());
        assertTrue("a.A.a --> a.A.b is missing", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.b"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node2));
        assertTrue("Feature a.A.b is not concrete", node2.isConfirmed());
        assertTrue("a.A.b <-- a.A.a is missing", node2.getInboundDependencies().contains(node1));

        node2.accept(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getClassNode().getPackageNode()));
        assertTrue("Package a is no longer concrete", node1.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()));
        
        assertTrue("Did not remove class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Did not remove class value", factory.getClasses().containsValue(node1.getClassNode()));
        assertTrue("Class a.A is no longer concrete", node1.getClassNode().isConfirmed());
        assertTrue("Class node no longer contains feature node a.A.a", node1.getClassNode().getFeatures().contains(node1));
        assertTrue("Class node no longer contains feature node a.A.b", node2.getClassNode().getFeatures().contains(node2));
        
        assertTrue("Remove feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Remove feature value", factory.getFeatures().containsValue(node1));
        assertTrue("Feature a.A.a is no longer concrete", node1.isConfirmed());
        assertTrue("Removed a.A.a --> a.A.b", node1.getOutboundDependencies().contains(node2));
        
        assertTrue("Remove feature key", factory.getFeatures().containsKey("a.A.b"));
        assertTrue("Remove feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.b is still concrete", node2.isConfirmed());
        assertTrue("Removed a.A.b <-- a.A.a", node2.getInboundDependencies().contains(node1));
    }

    public void testAcceptOutboundNonEmptyPackage() {
        PackageNode node1 = factory.createPackage("a", false);
        ClassNode node2 = factory.createClass("a.A", false);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1));
        assertFalse("Package a concrete", node1.isConfirmed());
        assertTrue("Package node does not contain class node", node1.getClasses().contains(node2));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node2));
        assertFalse("Class a.A is concrete", node2.isConfirmed());

        node1.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1));
        assertFalse("Package a is now concrete", node1.isConfirmed());
        assertTrue("Package node no longer contains class node", node1.getClasses().contains(node2));
        
        assertTrue("Removed class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value", factory.getClasses().containsValue(node2));
        assertFalse("Class a.A is now concrete", node2.isConfirmed());
    }

    public void testAcceptOutboundNonEmptyClass() {
        ClassNode node1 = factory.createClass("a.A", false);
        FeatureNode node2 = factory.createFeature("a.A.a", false);

        assertTrue("Missing package key a", factory.getPackages().containsKey("a"));
        assertTrue("Missing package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertFalse("Package a is concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node does not contain class node", node1.getPackageNode().getClasses().contains(node1));
        
        assertTrue("Missing class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Missing class value", factory.getClasses().containsValue(node1));
        assertFalse("Class a.A is concrete", node1.isConfirmed());
        assertTrue("Class node does not contain feature node", node1.getFeatures().contains(node2));
        
        assertTrue("Missing feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Missing feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.a is concrete", node2.isConfirmed());

        node1.acceptOutbound(visitor);
        
        assertTrue("Removed package key a", factory.getPackages().containsKey("a"));
        assertTrue("Removed package value a", factory.getPackages().containsValue(node1.getPackageNode()));
        assertFalse("Package a is now concrete", node1.getPackageNode().isConfirmed());
        assertTrue("Package node no longer contains class node", node1.getPackageNode().getClasses().contains(node1));
        
        assertTrue("Removed class key", factory.getClasses().containsKey("a.A"));
        assertTrue("Removed class value", factory.getClasses().containsValue(node1));
        assertFalse("Class a.A is now concrete", node1.isConfirmed());
        assertTrue("Class node no longer contains feature node", node1.getFeatures().contains(node2));
        
        assertTrue("Removed feature key", factory.getFeatures().containsKey("a.A.a"));
        assertTrue("Removed feature value", factory.getFeatures().containsValue(node2));
        assertFalse("Feature a.A.a is now concrete", node2.isConfirmed());
    }
}
