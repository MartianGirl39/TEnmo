package com.techelevator.exceptions;

public class TenmoRequestException extends RuntimeException {

    private int statusCode;

    public TenmoRequestException(String errorMessage, int statusCode){
        super(errorMessage);
        this.statusCode=statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
