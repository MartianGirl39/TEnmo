package com.techelevator.tenmo.model.dto;

public class TransferDto {
    private int account;
    private double amount;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }
}
