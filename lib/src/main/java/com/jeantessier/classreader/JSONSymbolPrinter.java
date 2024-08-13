package com.jeantessier.classreader;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class JSONSymbolPrinter extends SymbolPrinter {
    public JSONSymbolPrinter(PrintWriter out) {
        super(out);
    }

    public void print(SymbolGatherer gatherer) throws IOException {
        var output = "[" +
                gatherer.stream()
                        .map(this::visitableToRecord)
                        .map(this::recordToJSON)
                                .collect(joining(",")) +
        "]";
        getOut().println(output);
    }

    private String recordToJSON(Map<String, ?> record) {
        return "{" +
                record.entrySet().stream()
                        .map(entry -> {
                            if  (entry.getValue() instanceof Stream<?> stream) {
                                return "\"" + entry.getKey() + "\":[" + format(stream) + "]";
                            } else {
                                return "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"";
                            }
                        })
                        .collect(joining(", "))
                +
                "}";
    }
}
