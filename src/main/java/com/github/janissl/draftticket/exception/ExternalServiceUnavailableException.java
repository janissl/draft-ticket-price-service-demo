package com.github.janissl.draftticket.exception;

import java.io.IOException;

public class ExternalServiceUnavailableException extends IOException {
    public ExternalServiceUnavailableException(String msg) {
        super(msg);
    }
}
