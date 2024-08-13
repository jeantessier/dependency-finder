package com.jeantessier.classreader;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public abstract class SymbolPrinter {
    private static final Map<Class<? extends Visitable>, Collection<String>> HEADERS = Map.of(
            Classfile.class, List.of("symbol", "simple name"),
            Field_info.class, List.of("symbol", "class", "name", "type"),
            Method_info.class, List.of("symbol", "class", "name", "signature", "parameter types"),
            LocalVariable.class, List.of("symbol", "method", "name", "type"),
            InnerClass.class, List.of("symbol", "outer class", "inner name")
    );

    private final PrintWriter out;

    public SymbolPrinter(PrintWriter out) {
        this.out = out;
    }

    public PrintWriter getOut() {
        return out;
    }

    public abstract void print(SymbolGatherer gatherer) throws IOException;

    protected Stream<String> getHeadersFor(Class<? extends Visitable> visitableClass) {
        if (Classfile.class.isAssignableFrom(visitableClass)) {
            return HEADERS.get(Classfile.class).stream();
        } else if (Field_info.class.isAssignableFrom(visitableClass)) {
            return HEADERS.get(Field_info.class).stream();
        } else if (Method_info.class.isAssignableFrom(visitableClass)) {
            return HEADERS.get(Method_info.class).stream();
        } else if (LocalVariable.class.isAssignableFrom(visitableClass)) {
            return HEADERS.get(LocalVariable.class).stream();
        } else if (InnerClass.class.isAssignableFrom(visitableClass)) {
            return HEADERS.get(InnerClass.class).stream();
        } else {
            return Stream.empty();
        }
    }

    protected Stream<String> getHeadersFor(Visitable visitable) {
        return getHeadersFor(visitable.getClass());
    }

    protected Stream<?> getValuesFor(Visitable visitable) {
        if (visitable instanceof Classfile classfile) {
            return Stream.of(
                    classfile,
                    classfile.getSimpleName()
            );
        } else if (visitable instanceof Field_info field) {
            return Stream.of(
                    field,
                    field.getClassfile(),
                    field.getName(),
                    field.getType()
            );
        } else if (visitable instanceof Method_info method) {
            return Stream.concat(
                    Stream.of(
                            method,
                            method.getClassfile(),
                            method.getName(),
                            method.getSignature(),
                            method.getReturnType()
                    ),
                    method.getParameterTypes()
            );
        } else if (visitable instanceof LocalVariable localVariable) {
            return Stream.of(
                    localVariable,
                    localVariable.getMethod(),
                    localVariable.getName(),
                    localVariable.getType()
            );
        } else if (visitable instanceof InnerClass innerClass) {
            return Stream.of(
                    innerClass,
                    innerClass.getOuterClassInfo(),
                    innerClass.getInnerName()
            );
        } else {
            return Stream.empty();
        }
    }

    protected String format(Stream<?> stream) {
        return stream
                .map(entry -> "\"" + entry + "\"")
                .collect(Collectors.joining(", "));
    }
}
