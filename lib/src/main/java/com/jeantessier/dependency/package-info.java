/**
 * <p>Build forests of packages, classes, and features and their dependencies.
 * Each node can cross-reference other nodes in the forest, according to dependencies
 * within the code.</p>
 *
 * <p>Here are the core abstractions.  At the code are <code>Node</code> and its
 * subclasses.  The <code>NodeFactory</code> creates <code>Node</code> instances.
 * The <code>Visitor</code> implementations traverse dependency graphs and perform
 * custom operations.  For instance, the <code>GraphSummarizer</code> is used to
 * perform queries and the <code>Printer</code> subclasses do textual rendering.</p>
 *
 * <div>
 * <img src="doc-files/Visitors.jpg" alt="Visitor Pattern" /><br />
 * <b>Visitors</b>
 * </div>
 *
 * <p><code>CodeDependencyCollector</code> traverses <code>.class</code> files and
 * builds the dependency graph along the way, using a <code>NodeFactory</code>.</p>
 *
 * <div>
 * <img src="doc-files/CodeDependencyCollector.jpg" alt="Sample object graph for extracting dependencies from code" /><br />
 * <b>CodeDependencyCollector</b>
 * </div>
 *
 * <p>Another way to build a dependency graph is to load one that was saved to an
 * XML document.  Dependency Finder uses SAX in the form of <code>NodeLoader</code>
 * and <code>NodeHandler</code>.  The latter, again, uses a <code>NodeFactory</code>
 * to build the actual in memory representation.</p>
 *
 * <div>
 * <img src="doc-files/SAX.jpg" alt="Sample object graph for loading dependencies from XML file" /><br />
 * <b>SAX</b>
 * </div>
 */
package com.jeantessier.dependency;
