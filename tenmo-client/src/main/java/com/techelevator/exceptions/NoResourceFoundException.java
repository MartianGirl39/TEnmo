package com.techelevator.exceptions;

public class NoResourceFoundException extends TenmoRequestException {
    public NoResourceFoundException(String errorMessage) {
        super(errorMessage, 404);
    }
}
