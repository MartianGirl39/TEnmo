package com.techelevator.tenmo.model.dto;

public class UserAccountDto extends AccountDto {
    private double balance;

    public double getAmount() {
        return balance;
    }

    public void setBalance(double amount) {
        this.balance = amount;
    }
}
