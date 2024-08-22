package com.jeantessier.classreader;

import java.io.*;

public class TextSymbolPrinter extends SymbolPrinter {
    public TextSymbolPrinter(PrintWriter out) {
        super(out);
    }

    public void print(SymbolGatherer gatherer) throws IOException {
        gatherer.stream()
                .map(this::getTextFor)
                .sorted()
                .distinct()
                .forEach(getOut()::println);
    }

    private String getTextFor(Visitable visitable) {
        if (visitable instanceof Method_info method) {
            return method.getFullSignature();
        } else {
            return visitable.toString();
        }
    }
}
