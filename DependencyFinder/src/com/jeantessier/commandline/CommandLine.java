/*
 *  Copyright (c) 2001-2009, Jean Tessier
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
    private static final boolean DEFAULT_STRICT = true;

    private boolean strict;
    private ParameterStrategy parameterStrategy;

    private Map<String, CommandLineSwitch> map = new TreeMap<String, CommandLineSwitch>();

    public CommandLine() {
        this(DEFAULT_STRICT, new CollectingParameterStrategy());
    }

    public CommandLine(boolean strict) {
        this(strict, new CollectingParameterStrategy());
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

    public ToggleSwitch addToggleSwitch(String name) {
        return addSwitch(new ToggleSwitch(name));
    }

    public ToggleSwitch addToggleSwitch(String name, boolean defaultValue) {
        return addSwitch(new ToggleSwitch(name, defaultValue));
    }

    public SingleValueSwitch addSingleValueSwitch(String name) {
        return addSwitch(new SingleValueSwitch(name));
    }

    public SingleValueSwitch addSingleValueSwitch(String name, boolean mandatory) {
        return addSwitch(new SingleValueSwitch(name, mandatory));
    }

    public SingleValueSwitch addSingleValueSwitch(String name, String defaultValue) {
        return addSwitch(new SingleValueSwitch(name, defaultValue));
    }

    public SingleValueSwitch addSingleValueSwitch(String name, String defaultValue, boolean mandatory) {
        return addSwitch(new SingleValueSwitch(name, defaultValue, mandatory));
    }

    public OptionalValueSwitch addOptionalValueSwitch(String name) {
        return addSwitch(new OptionalValueSwitch(name));
    }

    public OptionalValueSwitch addOptionalValueSwitch(String name, boolean mandatory) {
        return addSwitch(new OptionalValueSwitch(name, mandatory));
    }

    public OptionalValueSwitch addOptionalValueSwitch(String name, String defaultValue) {
        return addSwitch(new OptionalValueSwitch(name, defaultValue));
    }

    public OptionalValueSwitch addOptionalValueSwitch(String name, String defaultValue, boolean mandatory) {
        return addSwitch(new OptionalValueSwitch(name, defaultValue, mandatory));
    }

    public MultipleValuesSwitch addMultipleValuesSwitch(String name) {
        return addSwitch(new MultipleValuesSwitch(name));
    }

    public MultipleValuesSwitch addMultipleValuesSwitch(String name, boolean mandatory) {
        return addSwitch(new MultipleValuesSwitch(name, mandatory));
    }

    public MultipleValuesSwitch addMultipleValuesSwitch(String name, String defaultValue) {
        return addSwitch(new MultipleValuesSwitch(name, defaultValue));
    }

    public MultipleValuesSwitch addMultipleValuesSwitch(String name, String defaultValue, boolean mandatory) {
        return addSwitch(new MultipleValuesSwitch(name, defaultValue, mandatory));
    }

    /**
     * Returns an {@link AliasSwitch} mapping name to switchNames.
     *
     * @param name the name of the new alias.
     * @param switchNames the switches that the alias maps to.
     * @return an AliasSwitch for the new alias.
     * @throws IllegalArgumentException if any switch name is unknown.
     *
     * @see AliasSwitch
     */
    public AliasSwitch addAliasSwitch(String name, String ... switchNames) {
        CommandLineSwitch[] switches = new CommandLineSwitch[switchNames.length];
        for (int i = 0; i < switchNames.length; i++) {
            switches[i] = getSwitch(switchNames[i], true);
        }

        return addSwitch(new AliasSwitch(name, switches));
    }

    private <T extends CommandLineSwitch> T addSwitch(T cls) {
        map.put(cls.getName(), cls);
        return cls;
    }

    /**
     * Returns a {@link CommandLineSwitch} matching name, if any.
     *
     * @param name the name of the switch to lookup.
     * @return a switch matching name.
     * @throws IllegalArgumentException if this CommandLine is strict and name is unknown.
     *
     * @see CommandLineSwitch
     */
    public CommandLineSwitch getSwitch(String name) {
        return getSwitch(name, isStrict());
    }

    /**
     * Returns a {@link CommandLineSwitch} matching name, if any.
     *
     * @param name the name of the CommandLineSwitch to lookup.
     * @param strict if true, will throw an exception if name is unknown.
     * @return a CommandLineSwitch matching name.
     * @throws IllegalArgumentException if strict is true and name is unknown.
     */
    public CommandLineSwitch getSwitch(String name, boolean strict) {
        CommandLineSwitch cls = map.get(name);

        if (cls == null) {
            if (strict) {
                throw new IllegalArgumentException("Unknown switch \"" + name + "\"");
            } else {
                cls = new OptionalValueSwitch(name);
                addSwitch(cls);
            }
        }

        return cls;
    }

    public boolean getToggleSwitch(String name) {
        boolean result = false;

        CommandLineSwitch cls = map.get(name);
        if (cls != null) {
            result = (Boolean) cls.getValue();
        }

        return result;
    }

    public String getSingleSwitch(String name) {
        return getStringSwitch(name);
    }

    public String getOptionalSwitch(String name) {
        return getStringSwitch(name);
    }

    public List<String> getMultipleSwitch(String name) {
        return getListSwitch(name);
    }

    private String getStringSwitch(String name) {
        String result = null;

        CommandLineSwitch cls = map.get(name);
        if (cls != null) {
            result =  cls.getValue().toString();
        }

        return result;
    }

    private List<String> getListSwitch(String name) {
        List<String> result = null;

        CommandLineSwitch cls = map.get(name);
        if (cls != null && cls.getValue() instanceof List) {
            result =  (List<String>) cls.getValue();
        }

        return result;
    }

    public boolean isPresent(String name) {
        boolean result = false;

        CommandLineSwitch cls = map.get(name);
        if (cls != null) {
            result = cls.isPresent();
        }

        return result;
    }

    public Set<String> getKnownSwitches() {
        return map.keySet();
    }

    public Collection<CommandLineSwitch> getSwitches() {
        return map.values();
    }

    public Set<String> getPresentSwitches() {
        Set<String> result = new TreeSet<String>();

        for (String name : getKnownSwitches()) {
            CommandLineSwitch cls = map.get(name);

            if (cls.isPresent()) {
                result.add(name);
            }
        }

        return result;
    }

    public List<String> getParameters() {
        return parameterStrategy.getParameters();
    }

    public Collection<CommandLineException> parse(String args[]) {
        Collection<CommandLineException> exceptions = new ArrayList<CommandLineException>();

        int i=0;
        while (i < args.length) {
            try {
                if (args[i].startsWith("-")) {
                    String name  = args[i].substring(1);
                    String value = null;

                    if (i+1 < args.length && !map.containsKey(args[i+1].substring(1))) {
                        value = args[i+1];
                    }

                    i += getSwitch(name).parse(value);
                } else {
                    i += parameterStrategy.accept(args[i]);
                }
            } catch (CommandLineException e) {
                exceptions.add(e);
                i++;
            }
        }

        // Checking that all manadatory switches are present
        for (CommandLineSwitch cls : map.values()) {
            try {
                cls.validate();
            } catch (CommandLineException e) {
                exceptions.add(e);
            }
        }

        // Checking that all mandatory parameters are present
        try {
            parameterStrategy.validate();
        } catch (CommandLineException e) {
            exceptions.add(e);
        }

        return exceptions;
    }

    public void accept(Visitor visitor) {
        visitor.visitCommandLine(this);
    }
}
