package com.jeantessier.classreader;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class YAMLSymbolPrinter extends SymbolPrinter {
    public static final String DEFAULT_INDENT_TEXT = "    ";

    private final String indentText;

    private int indentLevel = 0;

    public YAMLSymbolPrinter(PrintWriter out, String indentText) {
        super(out);
        this.indentText = indentText;
    }

    public void print(SymbolGatherer gatherer) throws IOException {
        indent().append("symbols:").eol();
        raiseIndent();
        gatherer.stream()
                .map(this::visitableToRecord)
                .forEach(this::printRecord);
        lowerIndent();
    }

    private void printRecord(Map<String, ?> record) {
        indent().append("-").eol();
        raiseIndent();

        record.forEach((k, value) -> {
            var key = k.replaceAll("\\s+", "-");

            if (value instanceof Stream<?> stream) {
                indent().append(key).append(":").eol();
                raiseIndent();

                stream.forEach(parameterType -> {
                    indent().append("- ").append(parameterType).eol();
                });

                lowerIndent();
            } else {
                indent().append(key).append(": ").append(formatText(value.toString())).eol();
            }
        });

        lowerIndent();
    }

    private YAMLSymbolPrinter append(Object obj) {
        getOut().print(obj);
        return this;
    }

    private YAMLSymbolPrinter append(String s) {
        getOut().print(s);
        return this;
    }

    private YAMLSymbolPrinter indent() {
        IntStream.rangeClosed(1, indentLevel).forEach(i -> append(indentText));
        return this;
    }

    private void eol() {
        getOut().println();
    }

    private void raiseIndent() {
        indentLevel++;
    }

    private void lowerIndent() {
        indentLevel--;
    }

    private String formatText(String name) {
        if (name.isEmpty()) {
            return "\"\"";
        }

        if (name.contains(": ")) {
            return "\"" + name + "\"";
        }

        return name;
    }
}
