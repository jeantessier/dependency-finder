/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeletingVisitor {
    private final NodeFactory factory = new NodeFactory();
    private final DeletingVisitor visitor = new DeletingVisitor(factory);

    @Test
    void testCreation() {
        assertSame(factory, visitor.getFactory(), "factory");
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
    @Test
    void testAcceptEmptyPackage() {
        var node = factory.createPackage("a", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node), "Missing package value");
        assertTrue(node.isConfirmed(), "Package not concrete");

        node.accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key");
        assertFalse(factory.getPackages().containsValue(node), "Did not remove package value");
        assertFalse(node.isConfirmed(), "Package is still concrete");
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
    @Test
    void testAcceptPackageWithClasses() {
        var node = factory.createClass("a.A", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node.getPackageNode()), "Missing package value");
        assertTrue(node.getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node.getPackageNode().getClasses().contains(node), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node), "Missing class value");
        assertTrue(node.isConfirmed(), "Class not concrete");

        node.getPackageNode().accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key");
        assertFalse(factory.getPackages().containsValue(node.getPackageNode()), "Did not remove package value");
        assertFalse(node.getPackageNode().isConfirmed(), "Package is still concrete");
        assertFalse(node.getPackageNode().getClasses().contains(node), "Package node still contains class node");

        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node), "Did not remove class value");
        assertFalse(node.isConfirmed(), "Class is still concrete");
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
    @Test
    void testAcceptEmptyClass() {
        var node1 = factory.createClass("a.A", true);
        var node2 = factory.createClass("a.B", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Missing package value");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node does not contain class node");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node");
        assertSame(node1.getPackageNode(), node2.getPackageNode(), "Classes have different package");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1), "Missing class value");
        assertTrue(node1.isConfirmed(), "Class not concrete");

        node1.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Missing package value");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package not concrete");
        assertFalse(node1.getPackageNode().getClasses().contains(node1), "Package node still contains class node");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node");
        assertSame(node1.getPackageNode(), node2.getPackageNode(), "Classes have different package");

        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node1), "Did not remove class value");
        assertFalse(node1.isConfirmed(), "Class is still concrete");
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
    @Test
    void testAcceptSingleEmptyClass() {
        var node = factory.createClass("a.A", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node.getPackageNode()), "Missing package value");
        assertTrue(node.getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node.getPackageNode().getClasses().contains(node), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node), "Missing class value");
        assertTrue(node.isConfirmed(), "Class not concrete");

        node.accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key");
        assertFalse(factory.getPackages().containsValue(node.getPackageNode()), "Did not remove package value");
        assertFalse(node.getPackageNode().isConfirmed(), "Package is still concrete");
        assertFalse(node.getPackageNode().getClasses().contains(node), "Package node still contains class node");

        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node), "Did not remove class value");
        assertFalse(node.isConfirmed(), "Class is still concrete");
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
    @Test
    void testAcceptClassWithFeature() {
        var node1 = factory.createFeature("a.A.a", true);
        var node2 = factory.createClass("a.B", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node");

        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class not concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node does not contain feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Missing feature value");
        assertTrue(node1.isConfirmed(), "Feature not concrete");

        node1.getClassNode().accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package not concrete");
        assertFalse(node1.getClassNode().getPackageNode().getClasses().contains(node1), "Package node still contains class node");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node");

        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node1.getClassNode()), "Did not remove class value");
        assertFalse(node1.getClassNode().isConfirmed(), "Class is still concrete");
        assertFalse(node1.getClassNode().getFeatures().contains(node1), "Class node still contains feature node");

        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key");
        assertFalse(factory.getFeatures().containsValue(node1), "Did not remove feature value");
        assertFalse(node1.isConfirmed(), "Feature is still concrete");
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
    @Test
    void testAcceptSingleClassWithFeature() {
        var node1 = factory.createFeature("a.A.a", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");

        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class not concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node does not contain feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Missing feature value");
        assertTrue(node1.isConfirmed(), "Feature not concrete");

        node1.getClassNode().accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key");
        assertFalse(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Did not remove package value");
        assertFalse(node1.getClassNode().getPackageNode().isConfirmed(), "Package is still concrete");
        assertFalse(node1.getClassNode().getPackageNode().getClasses().contains(node1), "Package node still contains class node");

        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node1.getClassNode()), "Did not remove class value");
        assertFalse(node1.getClassNode().isConfirmed(), "Class is still concrete");
        assertFalse(node1.getClassNode().getFeatures().contains(node1), "Class node still contains feature node");

        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key");
        assertFalse(factory.getFeatures().containsValue(node1), "Did not remove feature value");
        assertFalse(node1.isConfirmed(), "Feature is still concrete");
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
    @Test
    void testAcceptEmptyFeature() {
        var node1 = factory.createFeature("a.A.a", true);
        var node2 = factory.createFeature("a.A.b", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class not concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node does not contain feature node");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node does not contain feature node");
        assertSame(node1.getClassNode(), node2.getClassNode(), "Features have different class");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Missing feature value");
        assertTrue(node1.isConfirmed(), "Feature not concrete");

        node1.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");

        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class not concrete");
        assertFalse(node1.getClassNode().getFeatures().contains(node1), "Class node still contains feature node");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node does not contain feature node");
        assertSame(node1.getClassNode(), node2.getClassNode(), "Features have different class");

        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key");
        assertFalse(factory.getFeatures().containsValue(node1), "Did not remove feature value");
        assertFalse(node1.isConfirmed(), "Feature is still concrete");
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
    @Test
    void testAcceptSingleEmptyFeature() {
        var node = factory.createFeature("a.A.a", true);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key");
        assertTrue(factory.getPackages().containsValue(node.getClassNode().getPackageNode()), "Missing package value");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Package not concrete");
        assertTrue(node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node.getClassNode()), "Missing class value");
        assertTrue(node.getClassNode().isConfirmed(), "Class not concrete");
        assertTrue(node.getClassNode().getFeatures().contains(node), "Class node does not contain feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node), "Missing feature value");
        assertTrue(node.isConfirmed(), "Feature not concrete");

        node.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key");
        assertTrue(factory.getPackages().containsValue(node.getClassNode().getPackageNode()), "Removed package value");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Package is no longer concrete");
        assertTrue(node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()), "Package node no longer contains class node");

        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key");
        assertTrue(factory.getClasses().containsValue(node.getClassNode()), "Removed class value");
        assertTrue(node.getClassNode().isConfirmed(), "Class is no longer concrete");
        assertFalse(node.getClassNode().getFeatures().contains(node), "Class node still contains feature node");

        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key");
        assertFalse(factory.getFeatures().containsValue(node), "Did not remove feature value");
        assertFalse(node.isConfirmed(), "Feature is still concrete");
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
    @Test
    void testAcceptPackageWithDependencyOnConcretePackage() {
        var a = factory.createPackage("a", true);
        var b = factory.createPackage("b", true);

        a.addDependency(b);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a), "Missing package value a");
        assertTrue(a.isConfirmed(), "Package a not concrete");
        assertTrue(a.getOutboundDependencies().contains(b), "a --> b is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b), "Missing package value b");
        assertTrue(b.isConfirmed(), "Package b not concrete");
        assertTrue(b.getInboundDependencies().contains(a), "b <-- a is missing");

        a.accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(a), "Did not remove package value a");
        assertFalse(a.isConfirmed(), "Package a is still concrete");
        assertFalse(a.getOutboundDependencies().contains(b), "Did not remove a --> b");
        
        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(b), "Removed package value b");
        assertTrue(b.isConfirmed(), "Package b is no longer concrete");
        assertFalse(b.getInboundDependencies().contains(a), "Did not remove b <-- a");
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
    @Test
    void testAcceptPackageWithDependencyOnNonConcretePackage() {
        var a = factory.createPackage("a", true);
        var b = factory.createPackage("b", false);

        a.addDependency(b);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a), "Missing package value a");
        assertTrue(a.isConfirmed(), "Package a not concrete");
        assertTrue(a.getOutboundDependencies().contains(b), "a --> b is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b), "Missing package value b");
        assertFalse(b.isConfirmed(), "Package b is concrete");
        assertTrue(b.getInboundDependencies().contains(a), "b <-- a is missing");

        a.accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(a), "Did not remove package value a");
        assertFalse(a.isConfirmed(), "Package a is still concrete");
        assertFalse(a.getOutboundDependencies().contains(b), "Did not remove a --> b");
        
        assertFalse(factory.getPackages().containsKey("b"), "Did not remove package key b");
        assertFalse(factory.getPackages().containsValue(b), "Did not remove package value b");
        assertFalse(b.isConfirmed(), "Package b became concrete");
        assertFalse(b.getInboundDependencies().contains(a), "Did not remove b <-- a");
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
    @Test
    void testAcceptClassWithDependencyOnConcreteClass() {
        var a_A = factory.createClass("a.A", true);
        var b_B = factory.createClass("b.B", true);

        a_A.addDependency(b_B);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a_A.getPackageNode()), "Missing package value a");
        assertTrue(a_A.getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A), "Missing class value a.A");
        assertTrue(a_A.isConfirmed(), "Class a.A not concrete");
        assertTrue(a_A.getOutboundDependencies().contains(b_B), "a.A --> b.B is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b_B.getPackageNode()), "Missing package value b");
        assertTrue(b_B.getPackageNode().isConfirmed(), "Package b not concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Missing class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B), "Missing class value b.B");
        assertTrue(b_B.isConfirmed(), "Class b.B not concrete");
        assertTrue(b_B.getInboundDependencies().contains(a_A), "b.B <-- a.A is missing");

        a_A.accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(a_A.getPackageNode()), "Did not remove package value a");
        assertFalse(a_A.getPackageNode().isConfirmed(), "Package a is still concrete");
        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key a.A");
        assertFalse(factory.getClasses().containsValue(a_A), "Did not remove class value a.A");
        assertFalse(a_A.isConfirmed(), "Class a.A is still concrete");
        assertFalse(a_A.getOutboundDependencies().contains(b_B), "Did not remove a.A --> b.B");
        
        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(b_B.getPackageNode()), "Removed package value b");
        assertTrue(b_B.isConfirmed(), "Package b is no longer concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Removed class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B), "Removed class value b.B");
        assertTrue(b_B.isConfirmed(), "Class b.B is no longer concrete");
        assertFalse(b_B.getInboundDependencies().contains(a_A), "Did not remove b.B <-- a.A");
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
    @Test
    void testAcceptClassWithDependencyOnReferencedClass() {
        var a_A = factory.createClass("a.A", true);
        var b_B = factory.createClass("b.B", false);

        a_A.addDependency(b_B);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a_A.getPackageNode()), "Missing package value a");
        assertTrue(a_A.getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A), "Missing class value a.A");
        assertTrue(a_A.isConfirmed(), "Class a.A not concrete");
        assertTrue(a_A.getOutboundDependencies().contains(b_B), "a.A --> b.B is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b_B.getPackageNode()), "Missing package value b");
        assertFalse(b_B.getPackageNode().isConfirmed(), "Package b is concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Missing class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B), "Missing class value b.B");
        assertFalse(b_B.isConfirmed(), "Class b.B is concrete");
        assertTrue(b_B.getInboundDependencies().contains(a_A), "b.B <-- a.A is missing");

        a_A.accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(a_A.getPackageNode()), "Did not remove package value a");
        assertFalse(a_A.getPackageNode().isConfirmed(), "Package a is still concrete");
        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key a.A");
        assertFalse(factory.getClasses().containsValue(a_A), "Did not remove class value a.A");
        assertFalse(a_A.isConfirmed(), "Class a.A is still concrete");
        assertFalse(a_A.getOutboundDependencies().contains(b_B), "Did not remove a.A --> b.B");
        
        assertFalse(factory.getPackages().containsKey("b"), "Did not remove package key b");
        assertFalse(factory.getPackages().containsValue(b_B.getPackageNode()), "Did not remove package value b");
        assertFalse(b_B.getPackageNode().isConfirmed(), "Package b is now concrete");
        assertFalse(factory.getClasses().containsKey("b.B"), "Did not remove class key b.B");
        assertFalse(factory.getClasses().containsValue(b_B), "Did not remove class value b.B");
        assertFalse(b_B.isConfirmed(), "Class b.B is now concrete");
        assertFalse(b_B.getInboundDependencies().contains(a_A), "Did not remove b.B <-- a.A");
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
    @Test
    void testAcceptClassWithFeatureWithDependencyOnConcreteFeature() {
        var a_A_a = factory.createFeature("a.A.a", true);
        var b_B_b = factory.createFeature("b.B.b", true);

        a_A_a.addDependency(b_B_b);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A_a.getClassNode()), "Missing class value a.A");
        assertTrue(a_A_a.getClassNode().isConfirmed(), "Class a.A not concrete");
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key a.A.a");
        assertTrue(factory.getFeatures().containsValue(a_A_a), "Missing feature value a.A.a");
        assertTrue(a_A_a.isConfirmed(), "Feature a.A.a not concrete");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "a.A.a --> b.B.b is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Missing package value b");
        assertTrue(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b not concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Missing class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B_b.getClassNode()), "Missing class value b.B");
        assertTrue(b_B_b.getClassNode().isConfirmed(), "Class b.B not concrete");
        assertTrue(factory.getFeatures().containsKey("b.B.b"), "Missing feature key b.B.b");
        assertTrue(factory.getFeatures().containsValue(b_B_b), "Missing feature value b.B.b");
        assertTrue(b_B_b.isConfirmed(), "Feature b.B.b not concrete");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "b.B <-- a.A is missing");

        a_A_a.getClassNode().accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Did not remove package value a");
        assertFalse(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a is still concrete");
        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key a.A");
        assertFalse(factory.getClasses().containsValue(a_A_a.getClassNode()), "Did not remove class value a.A");
        assertFalse(a_A_a.getClassNode().isConfirmed(), "Class a.A is still concrete");
        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key a.A.a");
        assertFalse(factory.getFeatures().containsValue(a_A_a), "Did not remove feature value a.A.a");
        assertFalse(a_A_a.isConfirmed(), "Feature a.A.a is still concrete");
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A --> b.B");
        
        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Removed package value b");
        assertTrue(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b is no longer concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Removed class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B_b.getClassNode()), "Removed class value b.B");
        assertTrue(b_B_b.getClassNode().isConfirmed(), "Class b.B is no longer concrete");
        assertTrue(factory.getFeatures().containsKey("b.B.b"), "Removed feature key b.B.b");
        assertTrue(factory.getFeatures().containsValue(b_B_b), "Removed feature value b.B.b");
        assertTrue(b_B_b.isConfirmed(), "Feature b.B.b is no longer concrete");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B <-- a.A");
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
    @Test
    void testAcceptFeatureWithDependencyOnConcreteFeature() {
        var a_A_a = factory.createFeature("a.A.a", true);
        var b_B_b = factory.createFeature("b.B.b", true);

        a_A_a.addDependency(b_B_b);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A_a.getClassNode()), "Missing class value a.A");
        assertTrue(a_A_a.getClassNode().isConfirmed(), "Class a.A not concrete");
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key a.A.a");
        assertTrue(factory.getFeatures().containsValue(a_A_a), "Missing feature value a.A.a");
        assertTrue(a_A_a.isConfirmed(), "Feature a.A.a not concrete");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "a.A.a --> b.B.b is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Missing package value b");
        assertTrue(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b not concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Missing class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B_b.getClassNode()), "Missing class value b.B");
        assertTrue(b_B_b.getClassNode().isConfirmed(), "Class b.B not concrete");
        assertTrue(factory.getFeatures().containsKey("b.B.b"), "Missing feature key b.B.b");
        assertTrue(factory.getFeatures().containsValue(b_B_b), "Missing feature value b.B.b");
        assertTrue(b_B_b.isConfirmed(), "Feature b.B.b not concrete");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "b.B <-- a.A is missing");

        a_A_a.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Removed package value a");
        assertTrue(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A_a.getClassNode()), "Removed class value a.A");
        assertTrue(a_A_a.getClassNode().isConfirmed(), "Class a.A is no longer concrete");
        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key a.A.a");
        assertFalse(factory.getFeatures().containsValue(a_A_a), "Did not remove feature value a.A.a");
        assertFalse(a_A_a.isConfirmed(), "Feature a.A.a is still concrete");
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A --> b.B");
        
        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Removed package value b");
        assertTrue(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b is no longer concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Removed class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B_b.getClassNode()), "Removed class value b.B");
        assertTrue(b_B_b.getClassNode().isConfirmed(), "Class b.B is no longer concrete");
        assertTrue(factory.getFeatures().containsKey("b.B.b"), "Removed feature key b.B.b");
        assertTrue(factory.getFeatures().containsValue(b_B_b), "Removed feature value b.B.b");
        assertTrue(b_B_b.isConfirmed(), "Feature b.B.b is no longer concrete");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B <-- a.A");
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
    @Test
    void testAcceptClassWithFeatureWithDependencyOnReferencedFeature() {
        var a_A_a = factory.createFeature("a.A.a", true);
        var b_B_b = factory.createFeature("b.B.b", false);

        a_A_a.addDependency(b_B_b);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A_a.getClassNode()), "Missing class value a.A");
        assertTrue(a_A_a.getClassNode().isConfirmed(), "Class a.A not concrete");
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key a.A.a");
        assertTrue(factory.getFeatures().containsValue(a_A_a), "Missing feature value a.A.a");
        assertTrue(a_A_a.isConfirmed(), "Feature a.A.a not concrete");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "a.A.a --> b.B.b is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Missing package value b");
        assertFalse(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b is concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Missing class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B_b.getClassNode()), "Missing class value b.B");
        assertFalse(b_B_b.getClassNode().isConfirmed(), "Class b.B is concrete");
        assertTrue(factory.getFeatures().containsKey("b.B.b"), "Missing feature key b.B.b");
        assertTrue(factory.getFeatures().containsValue(b_B_b), "Missing feature value b.B.b");
        assertFalse(b_B_b.isConfirmed(), "Feature b.B.b is concrete");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "b.B <-- a.A is missing");

        a_A_a.getClassNode().accept(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Did not remove package value a");
        assertFalse(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a is still concrete");
        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key a.A");
        assertFalse(factory.getClasses().containsValue(a_A_a.getClassNode()), "Did not remove class value a.A");
        assertFalse(a_A_a.getClassNode().isConfirmed(), "Class a.A is still concrete");
        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key a.A.a");
        assertFalse(factory.getFeatures().containsValue(a_A_a), "Did not remove feature value a.A.a");
        assertFalse(a_A_a.isConfirmed(), "Feature a.A.a is still concrete");
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A --> b.B");
        
        assertFalse(factory.getPackages().containsKey("b"), "Did not remove package key b");
        assertFalse(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Did not remove package value b");
        assertFalse(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b is now concrete");
        assertFalse(factory.getClasses().containsKey("b.B"), "Did not remove class key b.B");
        assertFalse(factory.getClasses().containsValue(b_B_b.getClassNode()), "Did not remove class value b.B");
        assertFalse(b_B_b.getClassNode().isConfirmed(), "Class b.B is now concrete");
        assertFalse(factory.getFeatures().containsKey("b.B.b"), "Did not remove feature key b.B.b");
        assertFalse(factory.getFeatures().containsValue(b_B_b), "Did not remove feature value b.B.b");
        assertFalse(b_B_b.isConfirmed(), "Feature b.B.b is now concrete");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B <-- a.A");
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
    @Test
    void testAcceptFeatureWithDependencyOnReferencedFeature() {
        var a_A_a = factory.createFeature("a.A.a", true);
        var b_B_b = factory.createFeature("b.B.b", false);

        a_A_a.addDependency(b_B_b);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A_a.getClassNode()), "Missing class value a.A");
        assertTrue(a_A_a.getClassNode().isConfirmed(), "Class a.A not concrete");
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key a.A.a");
        assertTrue(factory.getFeatures().containsValue(a_A_a), "Missing feature value a.A.a");
        assertTrue(a_A_a.isConfirmed(), "Feature a.A.a not concrete");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "a.A.a --> b.B.b is missing");

        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Missing package value b");
        assertFalse(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b is concrete");
        assertTrue(factory.getClasses().containsKey("b.B"), "Missing class key b.B");
        assertTrue(factory.getClasses().containsValue(b_B_b.getClassNode()), "Missing class value b.B");
        assertFalse(b_B_b.getClassNode().isConfirmed(), "Class b.B is concrete");
        assertTrue(factory.getFeatures().containsKey("b.B.b"), "Missing feature key b.B.b");
        assertTrue(factory.getFeatures().containsValue(b_B_b), "Missing feature value b.B.b");
        assertFalse(b_B_b.isConfirmed(), "Feature b.B.b is concrete");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "b.B <-- a.A is missing");

        a_A_a.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(a_A_a.getClassNode().getPackageNode()), "Removed package value a");
        assertTrue(a_A_a.getClassNode().getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key a.A");
        assertTrue(factory.getClasses().containsValue(a_A_a.getClassNode()), "Removed class value a.A");
        assertTrue(a_A_a.getClassNode().isConfirmed(), "Class a.A is no longer concrete");
        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key a.A.a");
        assertFalse(factory.getFeatures().containsValue(a_A_a), "Did not remove feature value a.A.a");
        assertFalse(a_A_a.isConfirmed(), "Feature a.A.a is still concrete");
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A --> b.B");
        
        assertFalse(factory.getPackages().containsKey("b"), "Did not remove package key b");
        assertFalse(factory.getPackages().containsValue(b_B_b.getClassNode().getPackageNode()), "Did not remove package value b");
        assertFalse(b_B_b.getClassNode().getPackageNode().isConfirmed(), "Package b is now concrete");
        assertFalse(factory.getClasses().containsKey("b.B"), "Did not remove class key b.B");
        assertFalse(factory.getClasses().containsValue(b_B_b.getClassNode()), "Did not remove class value b.B");
        assertFalse(b_B_b.getClassNode().isConfirmed(), "Class b.B is now concrete");
        assertFalse(factory.getFeatures().containsKey("b.B.b"), "Did not remove feature key b.B.b");
        assertFalse(factory.getFeatures().containsValue(b_B_b), "Did not remove feature value b.B.b");
        assertFalse(b_B_b.isConfirmed(), "Feature b.B.b is now concrete");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B <-- a.A");
    }

    @Test
    void testAcceptOutboundConcretePackage() {
        var node = factory.createPackage("a", true);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node), "Missing package value a");
        assertTrue(node.isConfirmed(), "Package a not concrete");

        node.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node), "Removed package value a");
        assertTrue(node.isConfirmed(), "Package a is no longer concrete");
    }

    @Test
    void testAcceptOutboundConcreteClass() {
        var node = factory.createClass("a.A", true);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node.getPackageNode()), "Missing package value a");
        assertTrue(node.getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(node.getPackageNode().getClasses().contains(node), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node), "Missing class value");
        assertTrue(node.isConfirmed(), "Class not concrete");

        node.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node.getPackageNode()), "Removed package value a");
        assertTrue(node.getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node.getPackageNode().getClasses().contains(node), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key");
        assertTrue(factory.getClasses().containsValue(node), "Removed class value");
        assertTrue(node.isConfirmed(), "Class a.A is no longer concrete");
    }

    @Test
    void testAcceptOutboundConcreteFeature() {
        var node = factory.createFeature("a.A.a", true);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Package a not concrete");
        assertTrue(node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node.getClassNode()), "Missing class value");
        assertTrue(node.getClassNode().isConfirmed(), "Class not concrete");
        assertTrue(node.getClassNode().getFeatures().contains(node), "Class node does not contain feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node), "Missing feature value");
        assertTrue(node.isConfirmed(), "Feature not concrete");

        node.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node.getClassNode().getPackageNode()), "Removed package value a");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key");
        assertTrue(factory.getClasses().containsValue(node.getClassNode()), "Removed class value");
        assertTrue(node.getClassNode().isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node.getClassNode().getFeatures().contains(node), "Class node no longer contains feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Removed feature key");
        assertTrue(factory.getFeatures().containsValue(node), "Removed feature value");
        assertTrue(node.isConfirmed(), "Feature a.A.a is no longer concrete");
    }

    @Test
    void testAcceptOutboundEmptyNonConcretePackage() {
        var node = factory.createPackage("a", false);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node), "Missing package value a");
        assertFalse(node.isConfirmed(), "Package a concrete");

        node.acceptOutbound(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(node), "Did not remove package value a");
        assertFalse(node.isConfirmed(), "Package a is now concrete");
    }

    @Test
    void testAcceptOutboundEmptyNonConcreteClass() {
        var node = factory.createClass("a.A", false);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node.getPackageNode()), "Missing package value a");
        assertFalse(node.getPackageNode().isConfirmed(), "Package a is concrete");
        assertTrue(node.getPackageNode().getClasses().contains(node), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node), "Missing class value");
        assertFalse(node.isConfirmed(), "Class a.A is concrete");

        node.acceptOutbound(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(node.getPackageNode()), "Did not remove package value a");
        assertFalse(node.getPackageNode().isConfirmed(), "Package a is now concrete");
        assertFalse(node.getPackageNode().getClasses().contains(node), "Package node still contains class node");
        
        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node), "Did not remove class value");
        assertFalse(node.isConfirmed(), "Class a.A is now concrete");
    }

    @Test
    void testAcceptOutboundEmptyNonConcreteFeature() {
        var node = factory.createFeature("a.A.a", false);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node.getClassNode().getPackageNode()), "Missing package value a");
        assertFalse(node.getClassNode().getPackageNode().isConfirmed(), "Package a is concrete");
        assertTrue(node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node.getClassNode()), "Missing class value");
        assertFalse(node.getClassNode().isConfirmed(), "Class a.A is concrete");
        assertTrue(node.getClassNode().getFeatures().contains(node), "Class node does not contain feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node), "Missing feature value");
        assertFalse(node.isConfirmed(), "Feature a.A.a is concrete");

        node.acceptOutbound(visitor);
        
        assertFalse(factory.getPackages().containsKey("a"), "Did not remove package key a");
        assertFalse(factory.getPackages().containsValue(node.getClassNode().getPackageNode()), "Did not remove package value a");
        assertFalse(node.getClassNode().getPackageNode().isConfirmed(), "Package a is now concrete");
        assertFalse(node.getClassNode().getPackageNode().getClasses().contains(node.getClassNode()), "Package node still contains class node");
        
        assertFalse(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertFalse(factory.getClasses().containsValue(node.getClassNode()), "Did not remove class value");
        assertFalse(node.getClassNode().isConfirmed(), "Class a.A is now concrete");
        assertFalse(node.getClassNode().getFeatures().contains(node), "Class node still contains feature node");
        
        assertFalse(factory.getFeatures().containsKey("a.A.a"), "Did not remove feature key");
        assertFalse(factory.getFeatures().containsValue(node), "Did not remove feature value");
        assertFalse(node.isConfirmed(), "Feature a.A.a is now concrete");
    }

    @Test
    void testAcceptOutboundEmptyNonConcretePackageWithInboundDependency() {
        var node1 = factory.createPackage("a", true);
        var node2 = factory.createPackage("b", false);

        node1.addDependency(node2);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Missing package value a");
        assertTrue(node1.isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a --> b is missing");
        
        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(node2), "Missing package value b");
        assertFalse(node2.isConfirmed(), "Package b is concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "b <-- a is missing");

        node2.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Removed package value a");
        assertTrue(node1.isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a --> b");

        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(node2), "Removed package value b");
        assertFalse(node2.isConfirmed(), "Package b is now concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed b <-- a");
    }

    @Test
    void testAcceptOutboundEmptyNonConcreteClassWithInboundDependency() {
        var node1 = factory.createClass("a.A", true);
        var node2 = factory.createClass("a.B", false);

        node1.addDependency(node2);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Missing package value a");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node does not contain class node a.A");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node a.B");

        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1), "Missing class value");
        assertTrue(node1.isConfirmed(), "Class a.A is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a.A --> a.B is missing");

        assertTrue(factory.getClasses().containsKey("a.B"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node2), "Missing class value");
        assertFalse(node2.isConfirmed(), "Class a.B is concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "a.B <-- a.A is missing");

        node2.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Removed package value a");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node no longer contains class node a.A");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node no longer contains class node a.B");

        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key a.A");
        assertTrue(factory.getClasses().containsValue(node1), "Removed class value a.A");
        assertTrue(node1.isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a.A --> a.B");

        assertTrue(factory.getClasses().containsKey("a.B"), "Removed class key a.B");
        assertTrue(factory.getClasses().containsValue(node2), "Removed class value a.B");
        assertFalse(node2.isConfirmed(), "Class a.B is now concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed a.B <-- a.A");
    }

    @Test
    void testAcceptOutboundEmptyNonConcreteFeatureWithInboundDependency() {
        var node1 = factory.createFeature("a.A.a", true);
        var node2 = factory.createFeature("a.A.b", false);

        node1.addDependency(node2);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class a.A is not concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node does not contain feature node a.A.a");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node does not contain feature node a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Missing feature value");
        assertTrue(node1.isConfirmed(), "Feature a.A.a is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a.A.a --> a.A.b is missing");
        
        assertTrue(factory.getFeatures().containsKey("a.A.b"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Missing feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.b is concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "a.A.b <-- a.A.a is missing");

        node2.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Removed package value a");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Did not remove class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node no longer contains feature node a.A.a");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node no longer contains feature node a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Remove feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Remove feature value");
        assertTrue(node1.isConfirmed(), "Feature a.A.a is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a.A.a --> a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.b"), "Remove feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Remove feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.b is now concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed a.A.b <-- a.A.a");
    }

    @Test
    void testAcceptEmptyNonConcretePackageWithInboundDependency() {
        var node1 = factory.createPackage("a", true);
        var node2 = factory.createPackage("b", false);

        node1.addDependency(node2);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Missing package value a");
        assertTrue(node1.isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a --> b is missing");
        
        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(node2), "Missing package value b");
        assertFalse(node2.isConfirmed(), "Package b is concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "b <-- a is missing");

        node2.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Removed package value a");
        assertTrue(node1.isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a --> b");

        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(node2), "Removed package value b");
        assertFalse(node2.isConfirmed(), "Package b is now concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed b <-- a");
    }

    @Test
    void testAcceptEmptyNonConcreteClassWithInboundDependency() {
        var node1 = factory.createClass("a.A", true);
        var node2 = factory.createClass("a.B", false);

        node1.addDependency(node2);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Missing package value a");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node does not contain class node a.A");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node a.B");

        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1), "Missing class value");
        assertTrue(node1.isConfirmed(), "Class a.A is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a.A --> a.B is missing");

        assertTrue(factory.getClasses().containsKey("a.B"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node2), "Missing class value");
        assertFalse(node2.isConfirmed(), "Class a.B is concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "a.B <-- a.A is missing");

        node2.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Removed package value a");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node no longer contains class node a.A");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node no longer contains class node a.B");

        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key a.A");
        assertTrue(factory.getClasses().containsValue(node1), "Removed class value a.A");
        assertTrue(node1.isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a.A --> a.B");

        assertTrue(factory.getClasses().containsKey("a.B"), "Removed class key a.B");
        assertTrue(factory.getClasses().containsValue(node2), "Removed class value a.B");
        assertFalse(node2.isConfirmed(), "Class a.B is now concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed a.B <-- a.A");
    }

    @Test
    void testAcceptEmptyNonConcreteFeatureWithInboundDependency() {
        var node1 = factory.createFeature("a.A.a", true);
        var node2 = factory.createFeature("a.A.b", false);

        node1.addDependency(node2);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class a.A is not concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node does not contain feature node a.A.a");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node does not contain feature node a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Missing feature value");
        assertTrue(node1.isConfirmed(), "Feature a.A.a is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a.A.a --> a.A.b is missing");
        
        assertTrue(factory.getFeatures().containsKey("a.A.b"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Missing feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.b is concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "a.A.b <-- a.A.a is missing");

        node2.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Removed package value a");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Did not remove class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node no longer contains feature node a.A.a");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node no longer contains feature node a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Remove feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Remove feature value");
        assertTrue(node1.isConfirmed(), "Feature a.A.a is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a.A.a --> a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.b"), "Remove feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Remove feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.b is now concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed a.A.b <-- a.A.a");
    }

    @Test
    void testAcceptEmptyConcretePackageWithInboundDependency() {
        var node1 = factory.createPackage("a", true);
        var node2 = factory.createPackage("b", true);

        node1.addDependency(node2);
        
        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Missing package value a");
        assertTrue(node1.isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a --> b is missing");
        
        assertTrue(factory.getPackages().containsKey("b"), "Missing package key b");
        assertTrue(factory.getPackages().containsValue(node2), "Missing package value b");
        assertTrue(node2.isConfirmed(), "Package b is not concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "b <-- a is missing");

        node2.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Removed package value a");
        assertTrue(node1.isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a --> b");

        assertTrue(factory.getPackages().containsKey("b"), "Removed package key b");
        assertTrue(factory.getPackages().containsValue(node2), "Removed package value b");
        assertFalse(node2.isConfirmed(), "Package b is still concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed b <-- a");
    }

    @Test
    void testAcceptEmptyConcreteClassWithInboundDependency() {
        var node1 = factory.createClass("a.A", true);
        var node2 = factory.createClass("a.B", true);

        node1.addDependency(node2);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Missing package value a");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node does not contain class node a.A");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node does not contain class node a.B");

        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1), "Missing class value");
        assertTrue(node1.isConfirmed(), "Class a.A is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a.A --> a.B is missing");

        assertTrue(factory.getClasses().containsKey("a.B"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node2), "Missing class value");
        assertTrue(node2.isConfirmed(), "Class a.B is not concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "a.B <-- a.A is missing");

        node2.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Removed package value a");
        assertTrue(node1.getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node no longer contains class node a.A");
        assertTrue(node2.getPackageNode().getClasses().contains(node2), "Package node no longer contains class node a.B");

        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key a.A");
        assertTrue(factory.getClasses().containsValue(node1), "Removed class value a.A");
        assertTrue(node1.isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a.A --> a.B");

        assertTrue(factory.getClasses().containsKey("a.B"), "Removed class key a.B");
        assertTrue(factory.getClasses().containsValue(node2), "Removed class value a.B");
        assertFalse(node2.isConfirmed(), "Class a.B is still concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed a.B <-- a.A");
    }

    @Test
    void testAcceptEmptyConcreteFeatureWithInboundDependency() {
        var node1 = factory.createFeature("a.A.a", true);
        var node2 = factory.createFeature("a.A.b", true);

        node1.addDependency(node2);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Missing package value a");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package a is not concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Missing class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class a.A is not concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node does not contain feature node a.A.a");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node does not contain feature node a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Missing feature value");
        assertTrue(node1.isConfirmed(), "Feature a.A.a is not concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "a.A.a --> a.A.b is missing");
        
        assertTrue(factory.getFeatures().containsKey("a.A.b"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Missing feature value");
        assertTrue(node2.isConfirmed(), "Feature a.A.b is not concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "a.A.b <-- a.A.a is missing");

        node2.accept(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getClassNode().getPackageNode()), "Removed package value a");
        assertTrue(node1.getClassNode().getPackageNode().isConfirmed(), "Package a is no longer concrete");
        assertTrue(node1.getClassNode().getPackageNode().getClasses().contains(node1.getClassNode()), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Did not remove class key");
        assertTrue(factory.getClasses().containsValue(node1.getClassNode()), "Did not remove class value");
        assertTrue(node1.getClassNode().isConfirmed(), "Class a.A is no longer concrete");
        assertTrue(node1.getClassNode().getFeatures().contains(node1), "Class node no longer contains feature node a.A.a");
        assertTrue(node2.getClassNode().getFeatures().contains(node2), "Class node no longer contains feature node a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Remove feature key");
        assertTrue(factory.getFeatures().containsValue(node1), "Remove feature value");
        assertTrue(node1.isConfirmed(), "Feature a.A.a is no longer concrete");
        assertTrue(node1.getOutboundDependencies().contains(node2), "Removed a.A.a --> a.A.b");
        
        assertTrue(factory.getFeatures().containsKey("a.A.b"), "Remove feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Remove feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.b is still concrete");
        assertTrue(node2.getInboundDependencies().contains(node1), "Removed a.A.b <-- a.A.a");
    }

    @Test
    void testAcceptOutboundNonEmptyPackage() {
        var node1 = factory.createPackage("a", false);
        var node2 = factory.createClass("a.A", false);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Missing package value a");
        assertFalse(node1.isConfirmed(), "Package a concrete");
        assertTrue(node1.getClasses().contains(node2), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node2), "Missing class value");
        assertFalse(node2.isConfirmed(), "Class a.A is concrete");

        node1.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1), "Removed package value a");
        assertFalse(node1.isConfirmed(), "Package a is now concrete");
        assertTrue(node1.getClasses().contains(node2), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key");
        assertTrue(factory.getClasses().containsValue(node2), "Removed class value");
        assertFalse(node2.isConfirmed(), "Class a.A is now concrete");
    }

    @Test
    void testAcceptOutboundNonEmptyClass() {
        var node1 = factory.createClass("a.A", false);
        var node2 = factory.createFeature("a.A.a", false);

        assertTrue(factory.getPackages().containsKey("a"), "Missing package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Missing package value a");
        assertFalse(node1.getPackageNode().isConfirmed(), "Package a is concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node does not contain class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Missing class key");
        assertTrue(factory.getClasses().containsValue(node1), "Missing class value");
        assertFalse(node1.isConfirmed(), "Class a.A is concrete");
        assertTrue(node1.getFeatures().contains(node2), "Class node does not contain feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Missing feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Missing feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.a is concrete");

        node1.acceptOutbound(visitor);
        
        assertTrue(factory.getPackages().containsKey("a"), "Removed package key a");
        assertTrue(factory.getPackages().containsValue(node1.getPackageNode()), "Removed package value a");
        assertFalse(node1.getPackageNode().isConfirmed(), "Package a is now concrete");
        assertTrue(node1.getPackageNode().getClasses().contains(node1), "Package node no longer contains class node");
        
        assertTrue(factory.getClasses().containsKey("a.A"), "Removed class key");
        assertTrue(factory.getClasses().containsValue(node1), "Removed class value");
        assertFalse(node1.isConfirmed(), "Class a.A is now concrete");
        assertTrue(node1.getFeatures().contains(node2), "Class node no longer contains feature node");
        
        assertTrue(factory.getFeatures().containsKey("a.A.a"), "Removed feature key");
        assertTrue(factory.getFeatures().containsValue(node2), "Removed feature value");
        assertFalse(node2.isConfirmed(), "Feature a.A.a is now concrete");
    }
}
