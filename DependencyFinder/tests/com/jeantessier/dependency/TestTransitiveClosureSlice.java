/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import java.util.*;

import junit.framework.*;

public class TestTransitiveClosureSlice extends TestCase {
	private NodeFactory factory;
	
	private Node in3;
	private Node in2;
	private Node in1;
	private Node base;
	private Node out1;
	private Node out2;
	private Node out3;
	
    private TransitiveClosure selector;

	public TestTransitiveClosureSlice(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		factory = new NodeFactory();

		in3  = factory.CreatePackage("in3");
		in2  = factory.CreatePackage("in2");
		in1  = factory.CreatePackage("in1");
		base = factory.CreatePackage("base");
		out1 = factory.CreatePackage("out1");
		out2 = factory.CreatePackage("out2");
		out3 = factory.CreatePackage("out3");

		in3.AddDependency(in2);
		in2.AddDependency(in1);
		in1.AddDependency(base);
		base.AddDependency(out1);
		out1.AddDependency(out2);
		out2.AddDependency(out3);
		
		selector = new TransitiveClosure();
	}

	public void testDefaultDepth() {
		base.Accept(selector);

		assertEquals("number of packages", 4, selector.Factory().Packages().size());
		assertEquals("base.Inbound()",  0, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).Inbound().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).Outbound().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).Inbound().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).Outbound().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).Inbound().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).Outbound().size());
	}

	public void testDefaultDepthInboundOutbound() {
		selector.FollowInbounds(true);
		
		base.Accept(selector);

		assertEquals("number of packages", 7, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).Inbound().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).Outbound().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).Inbound().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).Outbound().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).Inbound().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).Outbound().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).Inbound().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).Outbound().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).Inbound().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).Outbound().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).Inbound().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).Outbound().size());
	}

	public void testZeroDepthInboundOutbound() {
		selector.FollowInbounds(true);
		selector.MaximumDepth(0);
		
		base.Accept(selector);

		assertEquals("number of packages", 1, selector.Factory().Packages().size());
		assertEquals("base.Inbound()",  0, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 0, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
	}

	public void testSingleDepthInboundOutbound() {
		selector.FollowInbounds(true);
		selector.MaximumDepth(1);
		
		base.Accept(selector);

		assertEquals("number of packages", 3, selector.Factory().Packages().size());
		assertEquals("in1.Inbound()",   0, ((Node) selector.Factory().Packages().get("in1")).Inbound().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).Outbound().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).Inbound().size());
		assertEquals("out1.Outbound()", 0, ((Node) selector.Factory().Packages().get("out1")).Outbound().size());
	}

	public void testDoubleDepthInboundOutbound() {
		selector.FollowInbounds(true);
		selector.MaximumDepth(2);
		
		base.Accept(selector);

		assertEquals("number of packages", 5, selector.Factory().Packages().size());
		assertEquals("in2.Inbound()",   0, ((Node) selector.Factory().Packages().get("in2")).Inbound().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).Outbound().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).Inbound().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).Outbound().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).Inbound().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).Outbound().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).Inbound().size());
		assertEquals("out2.Outbound()", 0, ((Node) selector.Factory().Packages().get("out2")).Outbound().size());
	}

	public void testExactDepthInboundOutbound() {
		selector.FollowInbounds(true);
		selector.MaximumDepth(3);
		
		base.Accept(selector);

		assertEquals("number of packages", 7, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).Inbound().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).Outbound().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).Inbound().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).Outbound().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).Inbound().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).Outbound().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).Inbound().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).Outbound().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).Inbound().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).Outbound().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).Inbound().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).Outbound().size());
	}

	public void testOverDepthInboundOutbound() {
		selector.FollowInbounds(true);
		selector.MaximumDepth(4);
		
		base.Accept(selector);

		assertEquals("number of packages", 7, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).Inbound().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).Outbound().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).Inbound().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).Outbound().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).Inbound().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).Outbound().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).Inbound().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).Outbound().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).Inbound().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).Outbound().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).Inbound().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).Outbound().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).Inbound().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).Outbound().size());
	}
}
