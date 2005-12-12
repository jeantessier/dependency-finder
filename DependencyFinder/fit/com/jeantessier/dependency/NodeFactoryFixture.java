package com.jeantessier.dependency;

import fit.*;

/**
 * Created by IntelliJ IDEA.
 * User: jeantessier
 * Date: Dec 7, 2005
 * Time: 10:25:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodeFactoryFixture extends DoFixture {
    public NodeFactoryFixture() {
        setSystemUnderTest(new NodeFactory());
    }

    public void sourceDependsOn(Node source, Node target) {
        source.addDependency(target);
    }

    public SetFixture inboundDependenciesTo(Node node) {
        return new SetFixture(node.getInboundDependencies());
    }

    public SetFixture outboundDependenciesFrom(Node node) {
        return new SetFixture(node.getOutboundDependencies());
    }

    public Object parse(String s, Class type) throws Exception {
        Object result = ((NodeFactory) systemUnderTest).getPackages().get(s);

        if (result == null) {
            result = ((NodeFactory) systemUnderTest).getClasses().get(s);
        }

        if (result == null) {
            result = ((NodeFactory) systemUnderTest).getFeatures().get(s);
        }

        if (result == null) {
            result = super.parse(s, type);
        }

        return result;
    }
}
