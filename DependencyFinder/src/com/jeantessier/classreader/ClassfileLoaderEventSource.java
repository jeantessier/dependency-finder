/*
 *  Copyright (c) 2001-2006, Jean Tessier
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
    
    private ClassfileLoaderDispatcher dispatcher;
    
    private ClassfileLoader dirLoader = new DirectoryClassfileLoader(this);
    private ClassfileLoader jarLoader = new JarClassfileLoader(this);
    private ClassfileLoader zipLoader = new ZipClassfileLoader(this);

    private HashSet loadListeners = new HashSet();

    private LinkedList groupNames = new LinkedList();
    private LinkedList groupSizes = new LinkedList();

    private int    previousDispatch;
    
    public ClassfileLoaderEventSource() {
        this(DEFAULT_DISPATCHER);
    }

    public ClassfileLoaderEventSource(ClassfileLoaderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    protected void load(String filename) {
        int dispatch = dispatcher.dispatch(filename);

        previousDispatch = dispatch;
        
        switch (dispatch) {
            case ClassfileLoaderDispatcher.ACTION_IGNORE:
                Logger.getLogger(getClass()).debug("IGNORE \"" + filename + "\"");
                break;

            case ClassfileLoaderDispatcher.ACTION_CLASS:
            case ClassfileLoaderDispatcher.ACTION_DIRECTORY:
                Logger.getLogger(getClass()).debug("DIRECTORY or CLASS \"" + filename + "\"");
                dirLoader.load(filename);
                break;

            case ClassfileLoaderDispatcher.ACTION_ZIP:
                Logger.getLogger(getClass()).debug("ZIP \"" + filename + "\"");
                zipLoader.load(filename);
                break;

            case ClassfileLoaderDispatcher.ACTION_JAR:
                Logger.getLogger(getClass()).debug("JAR \"" + filename + "\"");
                jarLoader.load(filename);
                break;

            default:
                Logger.getLogger(getClass()).debug("default (IGNORE) \"" + filename + "\"");
                break;
        }
    }

    protected void load(String filename, InputStream in) {
        int dispatch = dispatcher.dispatch(filename);

        if (dispatch == ClassfileLoaderDispatcher.ACTION_IGNORE && getTopGroupSize() == 1 &&  filename.equals(getTopGroupName())) {
            dispatch = previousDispatch;
        }
        
        switch (dispatch) {
            case ClassfileLoaderDispatcher.ACTION_IGNORE:
                Logger.getLogger(getClass()).debug("IGNORE \"" + filename + "\"");
                break;

            case ClassfileLoaderDispatcher.ACTION_DIRECTORY:
                Logger.getLogger(getClass()).debug("DIRECTORY \"" + filename + "\"");
                dirLoader.load(filename, in);
                break;

            case ClassfileLoaderDispatcher.ACTION_ZIP:
                Logger.getLogger(getClass()).debug("ZIP \"" + filename + "\"");
                zipLoader.load(filename, in);
                break;

            case ClassfileLoaderDispatcher.ACTION_JAR:
                Logger.getLogger(getClass()).debug("JAR \"" + filename + "\"");
                jarLoader.load(filename, in);
                break;

            case ClassfileLoaderDispatcher.ACTION_CLASS:
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

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).beginSession(event);
        }
    }

    protected void fireBeginGroup(String groupName, int size) {
        Logger.getLogger(getClass()).debug("Begin group \"" + groupName + "\" of size " + size);

        LoadEvent event = new LoadEvent(this, groupName, size);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).beginGroup(event);
        }

        pushGroupName(groupName);
        pushGroupSize(size);
    }
    
    protected void fireBeginFile(String filename) {
        Logger.getLogger(getClass()).debug("Begin file \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).beginFile(event);
        }
    }
    
    protected void fireBeginClassfile(String filename) {
        Logger.getLogger(getClass()).debug("Begin classfile \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).beginClassfile(event);
        }
    }

    protected void fireEndClassfile(String filename, Classfile classfile) {
        Logger.getLogger(getClass()).debug("End classfile \"" + filename + "\": " + ((classfile != null) ? classfile.getClassName() : "nothing"));
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, classfile);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).endClassfile(event);
        }
    }

    protected void fireEndFile(String filename) {
        Logger.getLogger(getClass()).debug("End file \"" + filename + "\"");
        
        LoadEvent event = new LoadEvent(this, getTopGroupName(), filename, null);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).endFile(event);
        }
    }

    protected void fireEndGroup(String groupName) {
        Logger.getLogger(getClass()).debug("End group \"" + groupName + "\"");
        
        LoadEvent event = new LoadEvent(this, groupName, null, null);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).endGroup(event);
        }

        popGroupName();
        popGroupSize();
    }

    protected void fireEndSession() {
        Logger.getLogger(getClass()).debug("End session");
        
        LoadEvent event = new LoadEvent(this, null, null, null);

        HashSet listeners;
        synchronized(loadListeners) {
            listeners = (HashSet) loadListeners.clone();
        }

        Iterator i = listeners.iterator();
        while(i.hasNext()) {
            ((LoadListener) i.next()).endSession(event);
        }
    }

    private String getTopGroupName() {
        String result = null;

        if (!groupNames.isEmpty()) {
            result = (String) groupNames.getLast();
        }

        return result;
    }

    private void pushGroupName(String groupName) {
        groupNames.addLast(groupName);
    }

    private String popGroupName() {
        return (String) groupNames.removeLast();
    }

    private int getTopGroupSize() {
        Integer result = null;

        if (!groupSizes.isEmpty()) {
            result = (Integer) groupSizes.getLast();
        }

        return result.intValue();
    }

    private void pushGroupSize(int size) {
        groupSizes.addLast(new Integer(size));
    }

    private int popGroupSize() {
        return ((Integer) groupSizes.removeLast()).intValue();
    }
}
