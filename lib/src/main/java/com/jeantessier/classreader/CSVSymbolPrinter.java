package com.jeantessier.classreader;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class CSVSymbolPrinter extends SymbolPrinter {
    private final boolean printClasses;
    private final boolean printFields;
    private final boolean printMethods;
    private final boolean printLocalVariables;
    private final boolean printInnerClasses;
    private final Optional<String> outPrefix;

    public CSVSymbolPrinter(PrintWriter out, boolean printClasses, boolean printFields, boolean printMethods, boolean printLocalVariables, boolean printInnerClasses, Optional<String> outPrefix) {
        super(out);
        this.printClasses = printClasses;
        this.printFields = printFields;
        this.printMethods = printMethods;
        this.printLocalVariables = printLocalVariables;
        this.printInnerClasses = printInnerClasses;
        this.outPrefix = outPrefix;
    }

    public void print(SymbolGatherer gatherer) throws IOException {
        var symbolMap = gatherer.stream()
                .collect(groupingBy(symbol -> {
                    if (symbol instanceof Classfile) {
                        return Classfile.class;
                    }
                    if (symbol instanceof Field_info) {
                        return Field_info.class;
                    }
                    if (symbol instanceof Method_info) {
                        return Method_info.class;
                    }
                    if (symbol instanceof LocalVariable) {
                        return LocalVariable.class;
                    }
                    if (symbol instanceof InnerClass) {
                        return InnerClass.class;
                    }
                    return Object.class;
                }));

        printCSVFile(printClasses, "classes", "Classes", Classfile.class, symbolMap.get(Classfile.class));
        printCSVFile(printFields, "fields", "Fields", Field_info.class, symbolMap.get(Field_info.class));
        printCSVFile(printMethods, "methods", "Methods", Method_info.class, symbolMap.get(Method_info.class));
        printCSVFile(printLocalVariables, "local_variables", "Local Variables", LocalVariable.class, symbolMap.get(LocalVariable.class));
        printCSVFile(printInnerClasses, "inner_classes", "Inner Classes", InnerClass.class, symbolMap.get(InnerClass.class));
    }

    private void printCSVFile(boolean printFlag, String outSuffix, String sectionTitle, Class<? extends Visitable> visitableClass, Iterable<? extends Visitable> visitables) throws IOException {
        if (printFlag && visitables != null) {
            PrintWriter out;

            if (outPrefix.isPresent()) {
                out = new PrintWriter(new FileWriter(outPrefix.get() + "_" + outSuffix + ".csv"));
            } else {
                out = getOut();
                out.print(sectionTitle);
                out.println(":");
            }

            out.println(format(getHeadersFor(visitableClass)));
            visitables.forEach(visitable -> {
                out.println(format(getValuesFor(visitable).flatMap(e -> e instanceof Stream<?> stream ? stream : Stream.of(e))));
            });

            if (outPrefix.isPresent()) {
                out.close();
            } else {
                out.println();
            }
        }
    }
}
