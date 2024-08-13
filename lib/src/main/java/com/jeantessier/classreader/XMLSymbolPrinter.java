package com.jeantessier.classreader;

import java.io.*;
import java.util.Map;
import java.util.stream.*;

public class XMLSymbolPrinter extends SymbolPrinter {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "https://depfind.sourceforge.io/dtd";
    public static final String DEFAULT_INDENT_TEXT = "    ";

    private final String encoding;
    private final String dtdPrefix;
    private final String indentText;

    private int indentLevel = 0;

    public XMLSymbolPrinter(PrintWriter out, String encoding, String dtdPrefix, String indentText) {
        super(out);
        this.encoding = encoding;
        this.dtdPrefix = dtdPrefix;
        this.indentText = indentText;
    }

    public void print(SymbolGatherer gatherer) throws IOException {
        printHeader(encoding, dtdPrefix);

        indent().append("<symbols>").eol();
        raiseIndent();

        gatherer.stream()
                .map(this::visitableToRecord)
                .forEach(this::printRecord);

        lowerIndent();
        indent().append("</symbols>").eol();
    }

    private void printHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE symbols SYSTEM \"").append(dtdPrefix).append("/symbols.dtd\">").eol();
        eol();
    }

    private void printRecord(Map<String, ?> record) {
        var nodeName = record.get("symbol type").toString().replaceAll("\\s+", "-");

        indent().append("<").append(nodeName).append(">").eol();
        raiseIndent();

        record.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("symbol type"))
                .forEach(entry -> {
                    var key = entry.getKey().replaceAll("\\s+", "-");

                    if (entry.getValue() instanceof Stream<?> stream) {
                        indent().append("<parameter-types>").eol();
                        raiseIndent();

                        stream.forEach(parameterType -> {
                            indent().append("<parameter-type>").append(parameterType).append("</parameter-type>").eol();
                        });

                        lowerIndent();
                        indent().append("</parameter-types>").eol();
                    } else {
                        indent().append("<").append(key).append(">").append(entry.getValue()).append("</").append(key).append(">").eol();
                    }
                });

        lowerIndent();
        indent().append("</").append(nodeName).append(">").eol();
    }

    private XMLSymbolPrinter append(Object obj) {
        getOut().print(obj);
        return this;
    }

    private XMLSymbolPrinter append(String s) {
        getOut().print(s);
        return this;
    }

    private XMLSymbolPrinter indent() {
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
}
