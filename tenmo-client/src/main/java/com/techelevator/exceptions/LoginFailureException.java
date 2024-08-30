package com.techelevator.exceptions;

public class LoginFailureException extends TenmoRequestException{
    private int statusCode;

    public LoginFailureException(String errorMessage, int statusCode){
        super(errorMessage,statusCode);
        this.statusCode=statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
