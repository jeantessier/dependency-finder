/*
 *  Copyright (c) 2001-2005, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.commandline;

import java.util.*;

/**
 *  Command-line parser.
 */
public class CommandLine implements Visitable {
    private static final boolean           DEFAULT_STRICT             = true;
    private static final ParameterStrategy DEFAULT_PARAMETER_STRATEGY = new AnyParameterStrategy();

    private boolean           strict;
    private ParameterStrategy parameterStrategy;

    private List              parameters = new LinkedList();
    private Map               map        = new TreeMap();

    public CommandLine() {
        this(DEFAULT_STRICT, DEFAULT_PARAMETER_STRATEGY);
    }

    public CommandLine(boolean strict) {
        this(strict, DEFAULT_PARAMETER_STRATEGY);
    }

    public CommandLine(ParameterStrategy parameterStrategy) {
        this(DEFAULT_STRICT, parameterStrategy);
    }

    public CommandLine(boolean strict, ParameterStrategy parameterStrategy) {
        setStrict(strict);
        setParameterStrategy(parameterStrategy);
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public ParameterStrategy getParameterStrategy() {
        return parameterStrategy;
    }

    public void setParameterStrategy(ParameterStrategy parameterStrategy) {
        this.parameterStrategy = parameterStrategy;
    }

    public void addSwitch(String name, CommandLineSwitch cls) {
        map.put(name, cls);
    }

    public void addToggleSwitch(String name) {
        addSwitch(name, new ToggleSwitch());
    }

    public void addToggleSwitch(String name, boolean defaultValue) {
        addSwitch(name, new ToggleSwitch(defaultValue));
    }

    public void addSingleValueSwitch(String name) {
        addSwitch(name, new SingleValueSwitch());
    }

    public void addSingleValueSwitch(String name, boolean mandatory) {
        addSwitch(name, new SingleValueSwitch(mandatory));
    }

    public void addSingleValueSwitch(String name, String defaultValue) {
        addSwitch(name, new SingleValueSwitch(defaultValue));
    }

    public void addSingleValueSwitch(String name, String defaultValue, boolean mandatory) {
        addSwitch(name, new SingleValueSwitch(defaultValue, mandatory));
    }

    public void addOptionalValueSwitch(String name) {
        addSwitch(name, new OptionalValueSwitch());
    }

    public void addOptionalValueSwitch(String name, boolean mandatory) {
        addSwitch(name, new OptionalValueSwitch(mandatory));
    }

    public void addOptionalValueSwitch(String name, String defaultValue) {
        addSwitch(name, new OptionalValueSwitch(defaultValue));
    }

    public void addOptionalValueSwitch(String name, String defaultValue, boolean mandatory) {
        addSwitch(name, new OptionalValueSwitch(defaultValue, mandatory));
    }

    public void addMultipleValuesSwitch(String name) {
        map.put(name, new MultipleValuesSwitch());
    }

    public void addMultipleValuesSwitch(String name, boolean mandatory) {
        map.put(name, new MultipleValuesSwitch(mandatory));
    }

    public void addMultipleValuesSwitch(String name, String defaultValue) {
        map.put(name, new MultipleValuesSwitch(defaultValue));
    }

    public void addMultipleValuesSwitch(String name, String defaultValue, boolean mandatory) {
        map.put(name, new MultipleValuesSwitch(defaultValue, mandatory));
    }

    public CommandLineSwitch getSwitch(String name) {
        return (CommandLineSwitch) map.get(name);
    }

    public boolean getToggleSwitch(String name) {
        boolean result = false;

        CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
        if (cls != null) {
            result = ((Boolean) cls.getValue()).booleanValue();
        }

        return result;
    }

    public String getSingleSwitch(String name) {
        return getStringSwitch(name);
    }

    public String getOptionalSwitch(String name) {
        return getStringSwitch(name);
    }

    public List getMultipleSwitch(String name) {
        return getListSwitch(name);
    }

    private String getStringSwitch(String name) {
        String result = null;

        CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
        if (cls != null) {
            result =  cls.getValue().toString();
        }

        return result;
    }

    private List getListSwitch(String name) {
        List result = null;

        CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
        if (cls != null && cls.getValue() instanceof List) {
            result =  (List) cls.getValue();
        }

        return result;
    }

    public boolean isPresent(String name) {
        boolean result = false;

        CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
        if (cls != null) {
            result = cls.isPresent();
        }

        return result;
    }

    public Set getKnownSwitches() {
        return map.keySet();
    }

    public Set getPresentSwitches() {
        Set result = new TreeSet();

        Iterator i = getKnownSwitches().iterator();
        while (i.hasNext()) {
            String            name = (String) i.next();
            CommandLineSwitch cls  = (CommandLineSwitch) map.get(name);

            if (cls.isPresent()) {
                result.add(name);
            }
        }

        return result;
    }

    public List getParameters() {
        return parameters;
    }

    public void parse(String args[]) throws CommandLineException {
        parameters = new LinkedList();

        int i=0;
        while (i < args.length) {
            if (args[i].startsWith("-")) {
                String name  = args[i].substring(1);
                String value = null;

                if (i+1 < args.length && !map.containsKey(args[i+1].substring(1))) {
                    value = args[i+1];
                }

                CommandLineSwitch cls = (CommandLineSwitch) map.get(name);

                if (cls == null) {
                    if (isStrict()) {
                        throw new CommandLineException("Unknown switch \"" + args[i] + "\"");
                    } else {
                        cls = new OptionalValueSwitch();
                        map.put(name, cls);
                    }
                }

                i += cls.parse(name, value);
            } else if (parameterStrategy.accept(args[i])) {
                parameters.add(args[i]);
                i++;
            } else {
                throw new CommandLineException("Invalid parameter \"" + args[i] + "\"");
            }
        }

        // Checking that all manadatory switches are present
        Iterator j = map.keySet().iterator();
        while (j.hasNext()) {
            String            name = (String) j.next();
            CommandLineSwitch cls  = (CommandLineSwitch) map.get(name);

            if (cls.isMandatory() && !cls.isPresent()) {
                throw new CommandLineException("Missing mandatory switch \"" + name + "\"");
            }
        }

        // Checking that all mandatory parameters are present
        if (!parameterStrategy.isSatisfied()) {
            throw new CommandLineException("Missing mandatory parameters");
        }
    }

    public void accept(Visitor visitor) {
        visitor.visitCommandLine(this);
    }
}
