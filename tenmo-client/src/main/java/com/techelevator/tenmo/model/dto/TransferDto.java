package com.techelevator.tenmo.model.dto;

public class TransferDto {
    int account;
    double amount;

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
