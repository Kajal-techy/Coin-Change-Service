package com.coinChangeService.Coin.Change.Service.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"stackTrace", "cause", "suppressed", "localizedMessage"})
public class OutOfMoneyException extends RuntimeException {

    public OutOfMoneyException(String message) {
        super(message);
    }
}
