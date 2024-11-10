package org.tbank.exception;

public class InvalidConfirmationCodeException extends  RuntimeException {
    public InvalidConfirmationCodeException(String message) {
        super(message);
    }
}
