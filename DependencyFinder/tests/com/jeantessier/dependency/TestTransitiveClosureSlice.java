/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

	protected void setUp() throws Exception {
		factory = new NodeFactory();

		in3  = factory.CreatePackage("in3");
		in2  = factory.CreatePackage("in2");
		in1  = factory.CreatePackage("in1");
		base = factory.CreatePackage("base");
		out1 = factory.CreatePackage("out1");
		out2 = factory.CreatePackage("out2");
		out3 = factory.CreatePackage("out3");

		in3.addDependency(in2);
		in2.addDependency(in1);
		in1.addDependency(base);
		base.addDependency(out1);
		out1.addDependency(out2);
		out2.addDependency(out3);
		
		selector = new TransitiveClosure();
	}

	public void testDefaultDepth() {
		base.accept(selector);

		assertEquals("number of packages", 4, selector.Factory().Packages().size());
		assertEquals("base.Inbound()",  0, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).getInboundDependencies().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).getOutboundDependencies().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).getInboundDependencies().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).getOutboundDependencies().size());
	}

	public void testUnboundedDepthInboundOutbound() {
		selector.MaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		selector.MaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		
		base.accept(selector);

		assertEquals("number of packages", 7, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).getInboundDependencies().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).getOutboundDependencies().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).getInboundDependencies().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).getOutboundDependencies().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).getInboundDependencies().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).getOutboundDependencies().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).getInboundDependencies().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).getOutboundDependencies().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).getInboundDependencies().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).getOutboundDependencies().size());
	}

	public void testUnboundedDepthInbound() {
		selector.MaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		selector.MaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
		
		base.accept(selector);

		assertEquals("number of packages", 4, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).getInboundDependencies().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).getOutboundDependencies().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).getInboundDependencies().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).getOutboundDependencies().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).getInboundDependencies().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).getOutboundDependencies().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 0, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
	}

	public void testUnboundedDepthOutbound() {
		selector.MaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
		selector.MaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);

		base.accept(selector);

		assertEquals("number of packages", 4, selector.Factory().Packages().size());
		assertEquals("base.Inbound()",  0, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).getInboundDependencies().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).getOutboundDependencies().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).getInboundDependencies().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).getOutboundDependencies().size());
	}

	public void testZeroDepthInboundOutbound() {
		selector.MaximumInboundDepth(0);
		selector.MaximumOutboundDepth(0);
		
		base.accept(selector);

		assertEquals("number of packages", 1, selector.Factory().Packages().size());
		assertEquals("base.Inbound()",  0, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 0, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
	}

	public void testSingleDepthInboundOutbound() {
		selector.MaximumInboundDepth(1);
		selector.MaximumOutboundDepth(1);
		
		base.accept(selector);

		assertEquals("number of packages", 3, selector.Factory().Packages().size());
		assertEquals("in1.Inbound()",   0, ((Node) selector.Factory().Packages().get("in1")).getInboundDependencies().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).getOutboundDependencies().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 0, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
	}

	public void testDoubleDepthInboundOutbound() {
		selector.MaximumInboundDepth(2);
		selector.MaximumOutboundDepth(2);
		
		base.accept(selector);

		assertEquals("number of packages", 5, selector.Factory().Packages().size());
		assertEquals("in2.Inbound()",   0, ((Node) selector.Factory().Packages().get("in2")).getInboundDependencies().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).getOutboundDependencies().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).getInboundDependencies().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).getOutboundDependencies().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).getInboundDependencies().size());
		assertEquals("out2.Outbound()", 0, ((Node) selector.Factory().Packages().get("out2")).getOutboundDependencies().size());
	}

	public void testExactDepthInboundOutbound() {
		selector.MaximumInboundDepth(3);
		selector.MaximumOutboundDepth(3);
		
		base.accept(selector);

		assertEquals("number of packages", 7, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).getInboundDependencies().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).getOutboundDependencies().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).getInboundDependencies().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).getOutboundDependencies().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).getInboundDependencies().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).getOutboundDependencies().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).getInboundDependencies().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).getOutboundDependencies().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).getInboundDependencies().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).getOutboundDependencies().size());
	}

	public void testOverDepthInboundOutbound() {
		selector.MaximumInboundDepth(4);
		selector.MaximumOutboundDepth(4);
		
		base.accept(selector);

		assertEquals("number of packages", 7, selector.Factory().Packages().size());
		assertEquals("in3.Inbound()",   0, ((Node) selector.Factory().Packages().get("in3")).getInboundDependencies().size());
		assertEquals("in3.Outbound()",  1, ((Node) selector.Factory().Packages().get("in3")).getOutboundDependencies().size());
		assertEquals("in2.Inbound()",   1, ((Node) selector.Factory().Packages().get("in2")).getInboundDependencies().size());
		assertEquals("in2.Outbound()",  1, ((Node) selector.Factory().Packages().get("in2")).getOutboundDependencies().size());
		assertEquals("in1.Inbound()",   1, ((Node) selector.Factory().Packages().get("in1")).getInboundDependencies().size());
		assertEquals("in1.Outbound()",  1, ((Node) selector.Factory().Packages().get("in1")).getOutboundDependencies().size());
		assertEquals("base.Inbound()",  1, ((Node) selector.Factory().Packages().get("base")).getInboundDependencies().size());
		assertEquals("base.Outbound()", 1, ((Node) selector.Factory().Packages().get("base")).getOutboundDependencies().size());
		assertEquals("out1.Inbound()",  1, ((Node) selector.Factory().Packages().get("out1")).getInboundDependencies().size());
		assertEquals("out1.Outbound()", 1, ((Node) selector.Factory().Packages().get("out1")).getOutboundDependencies().size());
		assertEquals("out2.Inbound()",  1, ((Node) selector.Factory().Packages().get("out2")).getInboundDependencies().size());
		assertEquals("out2.Outbound()", 1, ((Node) selector.Factory().Packages().get("out2")).getOutboundDependencies().size());
		assertEquals("out3.Inbound()",  1, ((Node) selector.Factory().Packages().get("out3")).getInboundDependencies().size());
		assertEquals("out3.Outbound()", 0, ((Node) selector.Factory().Packages().get("out3")).getOutboundDependencies().size());
	}
}
