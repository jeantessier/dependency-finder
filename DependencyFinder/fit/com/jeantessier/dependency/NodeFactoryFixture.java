package com.jeantessier.dependency;

import java.util.*;

import fitlibrary.*;

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

    public SetFixture dependenciesFor(Node node) {
        Collection dependencies = new LinkedList();

        Iterator i;

        i = node.getInboundDependencies().iterator();
        while (i.hasNext()) {
            dependencies.add(new Dependency(node.getName(), Dependency.INBOUND, ((Node) i.next()).getName()));
        }

        i = node.getOutboundDependencies().iterator();
        while (i.hasNext()) {
            dependencies.add(new Dependency(node.getName(), Dependency.OUTBOUND, ((Node) i.next()).getName()));
        }

        return new SetFixture(dependencies);
    }

    public Node findNode(String s) {
        Node result = (Node) ((NodeFactory) systemUnderTest).getPackages().get(s);

        if (result == null) {
            result = (Node) ((NodeFactory) systemUnderTest).getClasses().get(s);
        }

        if (result == null) {
            result = (Node) ((NodeFactory) systemUnderTest).getFeatures().get(s);
        }

        return result;
    }
}
