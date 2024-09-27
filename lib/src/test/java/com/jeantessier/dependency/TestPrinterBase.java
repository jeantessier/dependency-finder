package com.jeantessier.dependency;

import java.io.*;
import java.nio.file.*;

public class TestPrinterBase {
    protected static final Path REPORTS_DIR = Paths.get("build/resources/test");

    protected final NodeFactory factory = new NodeFactory();
    protected final StringWriter out = new StringWriter();

    protected enum DependencyGraph {
        PACKAGE_TO_PACKAGE {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createPackage("outbound", confirmed).addDependency(factory.createPackage("inbound", confirmed));
                factory.createPackage("empty", confirmed);
            }
        },
        CLASS_TO_CLASS {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createClass("outbound.Outbound", confirmed).addDependency(factory.createClass("inbound.Inbound", confirmed));
                factory.createClass("empty.Empty", confirmed);
            }
        },
        FEATURE_TO_FEATURE {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createFeature("outbound.Outbound.outbound()", confirmed).addDependency(factory.createFeature("inbound.Inbound.inbound()", confirmed));
                factory.createFeature("empty.Empty.empty()", confirmed);
            }
        },
        MIXED {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createClass("outbound.Outbound", confirmed).addDependency(factory.createClass("inbound.Inbound", confirmed));
                factory.createFeature("outbound.Outbound.outbound()", confirmed).addDependency(factory.createFeature("inbound.Inbound.inbound()", confirmed));
                factory.createClass("empty.Empty", confirmed);
            }
        },
        ALL {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createPackage("outbound", confirmed).addDependency(factory.createPackage("inbound", confirmed));
                factory.createClass("outbound.Outbound", confirmed).addDependency(factory.createClass("inbound.Inbound", confirmed));
                factory.createFeature("outbound.Outbound.outbound()", confirmed).addDependency(factory.createFeature("inbound.Inbound.inbound()", confirmed));
                factory.createPackage("empty", confirmed);
            }
        };

        abstract void create(NodeFactory factory, boolean confirmed);
    }
}
