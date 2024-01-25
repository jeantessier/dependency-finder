/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

package com.jeantessier.classreader;

import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;

public abstract class ClassfileLoaderEventSource extends ClassfileLoader {
    public static final ClassfileLoaderDispatcher DEFAULT_DISPATCHER = new PermissiveDispatcher();
    
    private final ClassfileFactory factory;
    private final ClassfileLoaderDispatcher dispatcher;
    
    private final ClassfileLoader dirLoader = new DirectoryClassfileLoader(this);
    private final ClassfileLoader jarLoader = new JarClassfileLoader(this);
    private final ClassfileLoader zipLoader = new ZipClassfileLoader(this);

    private final HashSet<LoadListener> loadListeners = new HashSet<>();

    private final LinkedList<String> groupNames = new LinkedList<>();
    private final LinkedList<Integer> groupSizes = new LinkedList<>();

    private ClassfileLoaderAction previousDispatch;
    
    public ClassfileLoaderEventSource(ClassfileFactory factory) {
        this(factory, DEFAULT_DISPATCHER);
    }

    public ClassfileLoaderEventSource(ClassfileFactory factory, ClassfileLoaderDispatcher dispatcher) {
        this.factory = factory;
        this.dispatcher = dispatcher;
    }

    protected ClassfileFactory getFactory() {
        return factory;
    }

    protected void load(String filename) {
        ClassfileLoaderAction dispatch = dispatcher.dispatch(filename);

        previousDispatch = dispatch;
        
        switch (dispatch) {
            case IGNORE -> LogManager.getLogger(getClass()).debug("IGNORE \"" + filename + "\"");
            case CLASS, DIRECTORY -> {
                LogManager.getLogger(getClass()).debug("DIRECTORY or CLASS \"" + filename + "\"");
                dirLoader.load(filename);
            }
            case ZIP -> {
                LogManager.getLogger(getClass()).debug("ZIP \"" + filename + "\"");
                zipLoader.load(filename);
            }
            case JAR -> {
                LogManager.getLogger(getClass()).debug("JAR \"" + filename + "\"");
                jarLoader.load(filename);
            }
            default -> LogManager.getLogger(getClass()).debug("default (IGNORE) \"" + filename + "\"");
        }
    }

    protected void load(String filename, InputStream in) {
        ClassfileLoaderAction dispatch = dispatcher.dispatch(filename);

        if (dispatch == ClassfileLoaderAction.IGNORE && getTopGroupSize() == 1 &&  filename.equals(getTopGroupName())) {
            dispatch = previousDispatch;
        }
        
        switch (dispatch) {
            case IGNORE -> LogManager.getLogger(getClass()).debug("IGNORE \"" + filename + "\"");
            case DIRECTORY -> {
                LogManager.getLogger(getClass()).debug("DIRECTORY \"" + filename + "\"");
                dirLoader.load(filename, in);
            }
            case ZIP -> {
                LogManager.getLogger(getClass()).debug("ZIP \"" + filename + "\"");
                zipLoader.load(filename, in);
            }
            case JAR -> {
                LogManager.getLogger(getClass()).debug("JAR \"" + filename + "\"");
                jarLoader.load(filename, in);
            }
            case CLASS -> {
                LogManager.getLogger(getClass()).debug("CLASS \"" + filename + "\"");
                try {
                    fireBeginClassfile(filename);
                    Classfile classfile = load(new DataInputStream(in));
                    fireEndClassfile(filename, classfile);
                } catch (Exception ex) {
                    LogManager.getLogger(getClass()).warn("Cannot load class from file \"" + filename + "\"", ex);
                }
            }
            default -> LogManager.getLogger(getClass()).debug("default (IGNORE) \"" + filename + "\"");
        }
    }

    public void addLoadListener(LoadListener listener) {
        synchronized(loadListeners) {
            loadListeners.add(listener);
        }
    }

    public void removeLoadListener(LoadListener listener) {
        synchronized(loadListeners) {
            loadListeners.remove(listener);
        }
    }

    protected void fireBeginSession() {
        LogManager.getLogger(getClass()).debug("Begin session");
        
        var event = new LoadEvent(this, null, null, null);
        loadListeners.forEach(listener -> listener.beginSession(event));
    }

    protected void fireBeginGroup(String groupName, int size) {
        LogManager.getLogger(getClass()).debug("Begin group \"" + groupName + "\" of size " + size);

        LoadEvent event = new LoadEvent(this, groupName, size);
        loadListeners.forEach(listener -> listener.beginGroup(event));

        pushGroupName(groupName);
        pushGroupSize(size);
    }
    
    protected void fireBeginFile(String filename) {
        LogManager.getLogger(getClass()).debug("Begin file \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);
        loadListeners.forEach(listener -> listener.beginFile(event));
    }
    
    protected void fireBeginClassfile(String filename) {
        LogManager.getLogger(getClass()).debug("Begin classfile \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);
        loadListeners.forEach(listener -> listener.beginClassfile(event));
    }

    protected void fireEndClassfile(String filename, Classfile classfile) {
        LogManager.getLogger(getClass()).debug("End classfile \"" + filename + "\": " + ((classfile != null) ? classfile.getClassName() : "nothing"));
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, classfile);
        loadListeners.forEach(listener -> listener.endClassfile(event));
    }

    protected void fireEndFile(String filename) {
        LogManager.getLogger(getClass()).debug("End file \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);
        loadListeners.forEach(listener -> listener.endFile(event));
    }

    protected void fireEndGroup(String groupName) {
        LogManager.getLogger(getClass()).debug("End group \"" + groupName + "\"");
        
        LoadEvent event = new LoadEvent(this, groupName, null, null);
        loadListeners.forEach(listener -> listener.endGroup(event));

        popGroupName();
        popGroupSize();
    }

    protected void fireEndSession() {
        LogManager.getLogger(getClass()).debug("End session");
        
        LoadEvent event = new LoadEvent(this, null, null, null);
        loadListeners.forEach(listener -> listener.endSession(event));
    }

    private String getTopGroupName() {
        return groupNames.isEmpty() ? null : groupNames.getLast();
    }

    private void pushGroupName(String groupName) {
        groupNames.addLast(groupName);
    }

    private void popGroupName() {
        groupNames.removeLast();
    }

    private int getTopGroupSize() {
        return groupSizes.isEmpty() ? 0 : groupSizes.getLast();
    }

    private void pushGroupSize(int size) {
        groupSizes.addLast(size);
    }

    private void popGroupSize() {
        groupSizes.removeLast();
    }
}
