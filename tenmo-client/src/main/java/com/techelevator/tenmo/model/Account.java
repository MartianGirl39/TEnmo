package com.techelevator.tenmo.model;

public class Account {
    private int user_id;
    private int account_id;
    private double balance;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account\n" +
                "\t| id: " + account_id + "\n" +
                "\t| balance: " + balance;
    }
}
