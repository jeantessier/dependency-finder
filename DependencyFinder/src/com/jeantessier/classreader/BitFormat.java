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

public class BitFormat {
    public static final int  DEFAULT_MAX_LENGTH      = 32;
    public static final int  DEFAULT_GROUP_SIZE      =  8;
    public static final char DEFAULT_GROUP_SEPARATOR = ' ';

    private int  maxLength;
    private int  groupSize;
    private char groupSeparator;
    
    public BitFormat() {
        this(DEFAULT_MAX_LENGTH, DEFAULT_GROUP_SIZE, DEFAULT_GROUP_SEPARATOR);
    }

    public BitFormat(int maxLength) {
        this(maxLength, DEFAULT_GROUP_SIZE, DEFAULT_GROUP_SEPARATOR);
    }

    public BitFormat(int maxLength, int groupSize) {
        this(maxLength, groupSize, DEFAULT_GROUP_SEPARATOR);
    }
    
    public BitFormat(int maxLength, int groupSize, char groupSeparator) {
        this.maxLength      = maxLength;
        this.groupSize      = groupSize;
        this.groupSeparator = groupSeparator;
    }

    public String format(int n) {
        return format(Integer.toBinaryString(n).toCharArray());
    }
    
    public String format(long n) {
        return format(Long.toBinaryString(n).toCharArray());
    }

    private String format(char[] binaryString) {
        StringBuffer result = new StringBuffer();

        for (int i=0; i<maxLength; i++) {
            if (((maxLength - i) % groupSize == 0) && i > 0) {
                result.append(groupSeparator);
            }

            if (i < maxLength - binaryString.length) {
                result.append('0');
            } else {
                result.append(binaryString[binaryString.length - maxLength + i]);
            }
        }
        
        return result.toString();
    }
}
