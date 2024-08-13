package com.jeantessier.classreader;

import java.io.*;

public class TextSymbolPrinter extends SymbolPrinter {
    public TextSymbolPrinter(PrintWriter out) {
        super(out);
    }

    public void print(SymbolGatherer gatherer) throws IOException {
        gatherer.stream()
                .map(Object::toString)
                .sorted()
                .distinct()
                .forEach(getOut()::println);
    }
}
