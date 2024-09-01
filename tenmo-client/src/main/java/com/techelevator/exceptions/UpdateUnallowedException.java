package com.techelevator.exceptions;

public class UpdateUnallowedException extends TenmoRequestException {
    public UpdateUnallowedException(String errorMessage, int statusCode) {
        super(errorMessage, 405);
    }
}
