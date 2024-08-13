package com.jeantessier.classreader;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public abstract class SymbolPrinter {
    private static final Map<Class<? extends Visitable>, Collection<String>> HEADERS = Map.of(
            Classfile.class, List.of("symbol type", "symbol", "simple name"),
            Field_info.class, List.of("symbol type", "symbol", "class", "name", "type"),
            Method_info.class, List.of("symbol type", "symbol", "class", "name", "signature", "return type", "parameter types"),
            LocalVariable.class, List.of("symbol type", "symbol", "method", "name", "type"),
            InnerClass.class, List.of("symbol type", "symbol", "outer class", "inner name")
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
                    "class name",
                    classfile,
                    classfile.getSimpleName()
            );
        } else if (visitable instanceof Field_info field) {
            return Stream.of(
                    "field name",
                    field,
                    field.getClassfile(),
                    field.getName(),
                    field.getType()
            );
        } else if (visitable instanceof Method_info method) {
            return Stream.of(
                    "method name",
                    method,
                    method.getClassfile(),
                    method.getName(),
                    method.getSignature(),
                    method.getReturnType(),
                    method.getParameterTypes()
            );
        } else if (visitable instanceof LocalVariable localVariable) {
            return Stream.of(
                    "local variable name",
                    localVariable,
                    localVariable.getMethod(),
                    localVariable.getName(),
                    localVariable.getType()
            );
        } else if (visitable instanceof InnerClass innerClass) {
            return Stream.of(
                    "inner class name",
                    innerClass,
                    innerClass.getOuterClassInfo(),
                    innerClass.getInnerName()
            );
        } else {
            return Stream.empty();
        }
    }

    protected Map<String, ?> visitableToRecord(Visitable visitable) {
        Map<String, ? super Object> result = new LinkedHashMap<>();

        List<String> headers = getHeadersFor(visitable).toList();
        List<?> values = getValuesFor(visitable).toList();

        IntStream.range(0, headers.size())
                .forEach(i -> result.put(headers.get(i), values.get(i)));

        return result;
    }

    protected String format(Stream<?> stream) {
        return stream
                .map(entry -> "\"" + entry + "\"")
                .collect(Collectors.joining(", "));
    }
}
