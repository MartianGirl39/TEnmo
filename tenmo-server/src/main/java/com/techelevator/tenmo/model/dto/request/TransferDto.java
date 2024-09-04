package com.techelevator.tenmo.model.dto.request;

import com.techelevator.tenmo.model.Transfer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class TransferDto {
    @Min(2001)
    private int account;
    @Min(0)
    private double amount;
    private String message="";

    public TransferDto(){

    }

    public TransferDto(int account, double amount, String message){
        this.account = account;
        this.amount = amount;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
