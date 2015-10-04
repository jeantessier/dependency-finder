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

package com.jeantessier.metrics;

public enum BasicMeasurements {
    PACKAGES("P"),

    CLASSES("C"),
    PUBLIC_CLASSES("PuC"),
    PACKAGE_CLASSES("PaC"),
    FINAL_CLASSES("FC"),
    SUPER_CLASSES("SuperC"),
    ABSTRACT_CLASSES("AC"),
    SYNTHETIC_CLASSES("SynthC"),
    INTERFACES("I"),
    DEPRECATED_CLASSES("DC"),
    STATIC_CLASSES("SC"),

    PUBLIC_METHODS("PuM"),
    PROTECTED_METHODS("ProM"),
    PRIVATE_METHODS("PriM"),
    PACKAGE_METHODS("PaM"),
    FINAL_METHODS("FM"),
    ABSTRACT_METHODS("AM"),
    DEPRECATED_METHODS("DM"),
    SYNTHETIC_METHODS("SynthM"),
    STATIC_METHODS("SM"),
    SYNCHRONIZED_METHODS("SynchM"),
    NATIVE_METHODS("NM"),
    TRIVIAL_METHODS("TM"),

    ATTRIBUTES("A"),
    PUBLIC_ATTRIBUTES("PuA"),
    PROTECTED_ATTRIBUTES("ProA"),
    PRIVATE_ATTRIBUTES("PriA"),
    PACKAGE_ATTRIBUTES("PaA"),
    FINAL_ATTRIBUTES("FA"),
    DEPRECATED_ATTRIBUTES("DA"),
    SYNTHETIC_ATTRIBUTES("SynthA"),
    STATIC_ATTRIBUTES("SA"),
    TRANSIENT_ATTRIBUTES("TA"),
    VOLATILE_ATTRIBUTES("VA"),

    INNER_CLASSES("IC"),
    PUBLIC_INNER_CLASSES("PuIC"),
    PROTECTED_INNER_CLASSES("ProIC"),
    PRIVATE_INNER_CLASSES("PriIC"),
    PACKAGE_INNER_CLASSES("PaIC"),
    ABSTRACT_INNER_CLASSES("AIC"),
    FINAL_INNER_CLASSES("FIC"),
    STATIC_INNER_CLASSES("SIC"),

    DEPTH_OF_INHERITANCE("DOI"),
    SUBCLASSES("SUB"),
    CLASS_SLOC("class SLOC"),

    SLOC("SLOC"),
    PARAMETERS("PARAM"),
    LOCAL_VARIABLES("LVAR"),

    INBOUND_INTRA_PACKAGE_DEPENDENCIES("IIP"),
    INBOUND_EXTRA_PACKAGE_DEPENDENCIES("IEP"),
    OUTBOUND_INTRA_PACKAGE_DEPENDENCIES("OIP"),
    OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES("OEP"),

    INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES("IICM"),
    INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES("IIPM"),
    INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES("IEPM"),
    OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES("OICF"),
    OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES("OIPF"),
    OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES("OIPC"),
    OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES("OEPF"),
    OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES("OEPC"),

    GROUP_NAME_CHARACTER_COUNT("GNCC"),
    GROUP_NAME_WORD_COUNT("GNWC"),
    CLASS_NAME_CHARACTER_COUNT("CNCC"),
    CLASS_NAME_WORD_COUNT("CNWC"),
    METHOD_NAME_CHARACTER_COUNT("MNCC"),
    METHOD_NAME_WORD_COUNT("MNWC");

    private String abbreviation;

    private BasicMeasurements(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
