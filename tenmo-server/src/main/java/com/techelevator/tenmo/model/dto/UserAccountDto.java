package com.techelevator.tenmo.model.dto;

public class UserAccountDto extends AccountDto {
    double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
