package com.github.janissl.draftticket.exception;

import java.io.IOException;

public class InvalidUserInputException extends IOException {
    public InvalidUserInputException(String msg) {
        super(msg);
    }
}
