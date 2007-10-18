// Copyright 2007 Google Inc.
// All Rights Reserved.
package com.jeantessier.dependency;

public class MatchException extends RuntimeException {
    public MatchException() {
        super();
    }

    public MatchException(String message) {
        super(message);
    }
    
    public MatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatchException(Throwable cause) {
        super(cause);
    }
}
