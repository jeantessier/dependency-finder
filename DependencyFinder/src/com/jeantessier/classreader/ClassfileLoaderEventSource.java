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

package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

public abstract class ClassfileLoaderEventSource extends ClassfileLoader {
    public static final ClassfileLoaderDispatcher DEFAULT_DISPATCHER = new PermissiveDispatcher();
    
    private ClassfileFactory factory;
    private ClassfileLoaderDispatcher dispatcher;
    
    private ClassfileLoader dirLoader = new DirectoryClassfileLoader(this);
    private ClassfileLoader jarLoader = new JarClassfileLoader(this);
    private ClassfileLoader zipLoader = new ZipClassfileLoader(this);

    private HashSet<LoadListener> loadListeners = new HashSet<LoadListener>();

    private LinkedList<String> groupNames = new LinkedList<String>();
    private LinkedList<Integer> groupSizes = new LinkedList<Integer>();

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
            case IGNORE:
                Logger.getLogger(getClass()).debug("IGNORE \"" + filename + "\"");
                break;

            case CLASS:
            case DIRECTORY:
                Logger.getLogger(getClass()).debug("DIRECTORY or CLASS \"" + filename + "\"");
                dirLoader.load(filename);
                break;

            case ZIP:
                Logger.getLogger(getClass()).debug("ZIP \"" + filename + "\"");
                zipLoader.load(filename);
                break;

            case JAR:
                Logger.getLogger(getClass()).debug("JAR \"" + filename + "\"");
                jarLoader.load(filename);
                break;

            default:
                Logger.getLogger(getClass()).debug("default (IGNORE) \"" + filename + "\"");
                break;
        }
    }

    protected void load(String filename, InputStream in) {
        ClassfileLoaderAction dispatch = dispatcher.dispatch(filename);

        if (dispatch == ClassfileLoaderAction.IGNORE && getTopGroupSize() == 1 &&  filename.equals(getTopGroupName())) {
            dispatch = previousDispatch;
        }
        
        switch (dispatch) {
            case IGNORE:
                Logger.getLogger(getClass()).debug("IGNORE \"" + filename + "\"");
                break;

            case DIRECTORY:
                Logger.getLogger(getClass()).debug("DIRECTORY \"" + filename + "\"");
                dirLoader.load(filename, in);
                break;

            case ZIP:
                Logger.getLogger(getClass()).debug("ZIP \"" + filename + "\"");
                zipLoader.load(filename, in);
                break;

            case JAR:
                Logger.getLogger(getClass()).debug("JAR \"" + filename + "\"");
                jarLoader.load(filename, in);
                break;

            case CLASS:
                Logger.getLogger(getClass()).debug("CLASS \"" + filename + "\"");
                try {
                    fireBeginClassfile(filename);
                    Classfile classfile = load(new DataInputStream(in));
                    fireEndClassfile(filename, classfile);
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).warn("Cannot load class from file \"" + filename + "\"", ex);
                }
                break;
                
            default:
                Logger.getLogger(getClass()).debug("default (IGNORE) \"" + filename + "\"");
                break;
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
        Logger.getLogger(getClass()).debug("Begin session");
        
        LoadEvent event = new LoadEvent(this, null, null, null);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.beginSession(event);
        }
    }

    protected void fireBeginGroup(String groupName, int size) {
        Logger.getLogger(getClass()).debug("Begin group \"" + groupName + "\" of size " + size);

        LoadEvent event = new LoadEvent(this, groupName, size);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.beginGroup(event);
        }

        pushGroupName(groupName);
        pushGroupSize(size);
    }
    
    protected void fireBeginFile(String filename) {
        Logger.getLogger(getClass()).debug("Begin file \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.beginFile(event);
        }
    }
    
    protected void fireBeginClassfile(String filename) {
        Logger.getLogger(getClass()).debug("Begin classfile \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.beginClassfile(event);
        }
    }

    protected void fireEndClassfile(String filename, Classfile classfile) {
        Logger.getLogger(getClass()).debug("End classfile \"" + filename + "\": " + ((classfile != null) ? classfile.getClassName() : "nothing"));
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, classfile);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.endClassfile(event);
        }
    }

    protected void fireEndFile(String filename) {
        Logger.getLogger(getClass()).debug("End file \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.endFile(event);
        }
    }

    protected void fireEndGroup(String groupName) {
        Logger.getLogger(getClass()).debug("End group \"" + groupName + "\"");
        
        LoadEvent event = new LoadEvent(this, groupName, null, null);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.endGroup(event);
        }

        popGroupName();
        popGroupSize();
    }

    protected void fireEndSession() {
        Logger.getLogger(getClass()).debug("End session");
        
        LoadEvent event = new LoadEvent(this, null, null, null);

        HashSet<LoadListener> listeners;
        synchronized(loadListeners) {
            listeners = (HashSet<LoadListener>) loadListeners.clone();
        }

        for (LoadListener listener : listeners) {
            listener.endSession(event);
        }
    }

    private String getTopGroupName() {
        String result = null;

        if (!groupNames.isEmpty()) {
            result = groupNames.getLast();
        }

        return result;
    }

    private void pushGroupName(String groupName) {
        groupNames.addLast(groupName);
    }

    private String popGroupName() {
        return groupNames.removeLast();
    }

    private int getTopGroupSize() {
        int result = 0;

        if (!groupSizes.isEmpty()) {
            result = groupSizes.getLast();
        }

        return result;
    }

    private void pushGroupSize(int size) {
        groupSizes.addLast(size);
    }

    private int popGroupSize() {
        return groupSizes.removeLast();
    }
}
