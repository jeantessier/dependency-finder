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

import java.util.*;

public interface LoadListener extends EventListener {
    public void beginSession(LoadEvent event);

    /**
     *  <p>The loader is starting on a new group of files.
     *  For example, this can be a new JAR file or a
     *  collection of loose <code>.class</code> files.</p>
     *
     *  <p>The event's filename attribute points to the source
     *  or the group of files, such as the JAR file's name
     *  or the root directory of the loose files.</p>
     *
     *  <p>The element and classfile attributes are null.</p>
     */
    public void beginGroup(LoadEvent event);
    
    /**
     *  <p>The loader is starting on a new file.</p>
     *
     *  <p>The event's element attribute contains the name of
     *  the file being processed.</p>
     *  
     *  <p>The event's filename attribute points to the group
     *  of files that contains the current file.  For
     *  example, the JAR file's name or the root directory
     *  of loose files.</p>
     *
     *  <p>The classfile attribute is null.</p>
     */
    public void beginFile(LoadEvent event);
    
    /**
     *  <p>The loader is starting on a new <code>.class</code>
     *  file.</p>
     *
     *  <p>The event's element attribute contains the name of
     *  the <code>.class</code> file being processed.</p>
     *  
     *  <p>The event's filename attribute points to the group
     *  of files that contains the current file.  For
     *  example, the JAR file's name or the root directory
     *  of loose files.</p>
     *
     *  <p>The classfile attribute is null.</p>
     */
    public void beginClassfile(LoadEvent event);
    
    /**
     *  <p>The loader is finished loading a <code>.class</code>
     *  file.</p>
     *
     *  <p>The event's classfile attribute contains the newly
     *  loaded Classfile instance from the <code>.class</code>
     *  file.</p>
     *  
     *  <p>The event's filename attribute points to the group
     *  of files that contains the current file.  For
     *  example, the JAR file's name or the root directory
     *  of loose files.</p>
     *
     *  <p>The element attribute is null.</p>
     */
    public void endClassfile(LoadEvent event);
    
    /**
     *  <p>The loader is finished with a file.</p>
     *
     *  <p>The event's element attribute contains the name of
     *  the file being processed.</p>
     *  
     *  <p>The event's filename attribute points to the group
     *  of files that contains the current file.  For
     *  example, the JAR file's name or the root directory
     *  of loose files.</p>
     *
     *  <p>The event's classfile attribute may contains a newly
     *  loaded Classfile instance from the file.</p>
     */
    public void endFile(LoadEvent event);

    /**
     *  <p>The loader finished the group of files. For
     *  example, this can be a new JAR file or a
     *  collection of loose <code>.class</code> files.</p>
     *
     *  <p>The event's filename attribute points to the source
     *  or the group of files, such as the JAR file's name
     *  or the root directory of the loose files.</p>
     *
     *  <p>The element and classfile attributes are null.</p>
     */
    public void endGroup(LoadEvent event);

    public void endSession(LoadEvent event);
}
