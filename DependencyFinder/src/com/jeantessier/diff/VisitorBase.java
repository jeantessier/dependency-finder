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

package com.jeantessier.diff;

public abstract class VisitorBase implements Visitor {
    private int deprecatableLevel = 0;

    private boolean deprecated[]   = new boolean[4];
    private boolean undeprecated[] = new boolean[4];

    private void raiseDeprecatableLevel() {
        deprecatableLevel++;
    }

    private void lowerDeprecatableLevel() {
        deprecatableLevel--;
    }

    public boolean isDeprecated() {
        return deprecated[deprecatableLevel];
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated[deprecatableLevel] = deprecated;
    }
    
    public boolean isUndeprecated() {
        return undeprecated[deprecatableLevel];
    }

    public void setUndeprecated(boolean undeprecated) {
        this.undeprecated[deprecatableLevel] = undeprecated;
    }

    public void visitProjectDifferences(ProjectDifferences differences) {
        // Do nothing
    }
        
    public void visitPackageDifferences(PackageDifferences differences) {
        // Do nothing
    }

    public void visitFieldDifferences(FieldDifferences differences) {
        // Do nothing
    }
    
    public void visitConstructorDifferences(ConstructorDifferences differences) {
        // Do nothing
    }

    public void visitMethodDifferences(MethodDifferences differences) {
        // Do nothing
    }

    public void visitDeprecatableDifferences(DeprecatableDifferences differences) {
        raiseDeprecatableLevel();
        
        setDeprecated(differences.isNewDeprecation());
        setUndeprecated(differences.isRemovedDeprecation());

        differences.getComponent().accept(this);
        
        lowerDeprecatableLevel();
    }
}
