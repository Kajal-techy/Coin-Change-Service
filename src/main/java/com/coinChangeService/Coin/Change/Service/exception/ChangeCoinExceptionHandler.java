package com.coinChangeService.Coin.Change.Service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class ChangeCoinExceptionHandler {

    @ExceptionHandler(value = OutOfMoneyException.class)
    public ResponseEntity<OutOfMoneyException> outOfMoneyException(OutOfMoneyException exception) {
        log.info("Entering ChangeCoinExceptionHandler.OutOfMoneyException with parameter exception {}.", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception);
    }

    @ExceptionHandler(value = BillNotSupportedException.class)
    public ResponseEntity<BillNotSupportedException> billNotSupportedException(BillNotSupportedException exception) {
        log.info("Entering ChangeCoinExceptionHandler.BillNotSupportedException with parameter exception {}.", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<String> missingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.info("Entering ChangeCoinExceptionHandler.MissingServletRequestParameterException with parameter exception {}.", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.info("Entering ChangeCoinExceptionHandler.MethodArgumentTypeMismatchException with parameter exception {}.", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
