package com.techelevator.exceptions;

public class RequestedForbiddenAccessException extends TenmoRequestException {
    public RequestedForbiddenAccessException(String errorMessage) {
        super(errorMessage, 403);
    }
}
