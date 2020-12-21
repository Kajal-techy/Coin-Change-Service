package com.coinChangeService.Coin.Change.Service.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"stackTrace", "cause", "trace", "error", "suppressed", "localizedMessage"})
public class BillNotSupportedException extends RuntimeException {

    public BillNotSupportedException(String message) {
        super(message);
    }
}