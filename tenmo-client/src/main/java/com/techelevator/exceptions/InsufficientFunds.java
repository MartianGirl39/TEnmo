package com.techelevator.exceptions;

public class InsufficientFunds extends TenmoRequestException{

    public InsufficientFunds(String errorMessage) {
        super(errorMessage, 400);
    }
}
