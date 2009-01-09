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

package com.jeantessier.text;

import java.util.*;

import org.apache.log4j.*;

public class RegularExpressionParser {
    public static List<String> parseRE(String re) {
        List<String> result = new LinkedList<String>();

        Logger logger = Logger.getLogger(RegularExpressionParser.class);
        logger.debug("ParseRE \"" + re + "\"");

        int length = re.length();
        int start  = 0;
        int stop   = -1;

        while (start < length && stop < length) {
            String separator = null;

            // Locate begining & determine separator
            while (start < length && stop < start) {
                if (re.charAt(start) == 'm' && (start + 1) < length) {
                    separator = re.substring(start + 1, start + 2);
                    stop = start + 2;
                } else if (re.charAt(start) == '/') {
                    separator = "/";
                    stop = start + 1;
                } else {
                    start++;
                }
            }

            logger.debug("start is " + start);
            logger.debug("separator is " + separator);

            // Locate end
            while (stop < length && start < stop) {
                stop = re.indexOf(separator, stop);
                logger.debug("indexOf() is " + stop);

                if (stop == -1 || re.charAt(stop - 1) != '\\') {

                    if (stop == -1) {
                        stop = length;
                    } else {
                        // Look for modifiers
                        stop++;
                        while (stop < length && (re.charAt(stop) == 'g' ||
                                                 re.charAt(stop) == 'i' ||
                                                 re.charAt(stop) == 'm' ||
                                                 re.charAt(stop) == 'o' ||
                                                 re.charAt(stop) == 's' ||
                                                 re.charAt(stop) == 'x')) {
                            stop++;
                        }
                    }

                    logger.debug("stop is " + stop);

                    // Add candidate
                    logger.debug("candidate is \"" + re.substring(start, stop) + "\"");
                    result.add(re.substring(start, stop));

                    // Move start
                    start = stop + 1;
                } else {
                    stop++;
                }
            }
        }

        return result;
    }
}
