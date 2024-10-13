package org.tbank.exception;

public class CurrencyServiceUnavailableException extends RuntimeException {
    public CurrencyServiceUnavailableException(String message) {
        super(message);
    }
}
