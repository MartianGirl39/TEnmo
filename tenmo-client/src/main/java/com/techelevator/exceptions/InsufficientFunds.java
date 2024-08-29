package com.techelevator.exceptions;

public class InsufficientFunds extends TenmoRequestException{

    public InsufficientFunds(String errorMessage, int statusCode) {
        super(errorMessage, statusCode);
    }
}
