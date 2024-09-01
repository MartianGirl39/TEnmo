package com.techelevator.exceptions;

public class SqlDisconnectException extends TenmoRequestException {

    public SqlDisconnectException(String errorMessage) {
        super(errorMessage, 503);
    }
}
