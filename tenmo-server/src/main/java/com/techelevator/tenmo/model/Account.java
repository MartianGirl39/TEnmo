package com.techelevator.tenmo.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Account {
    @NotNull
    private int account_id;
    @NotNull
    private int user_id;
    @NotNull
    @Min(0)
    private double balance;

    public Account(){

    }

    public Account(int account_id, int user_id, double balance) {
        this.account_id = account_id;
        this.user_id = user_id;
        this.balance = balance;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return account_id == account.account_id && user_id == account.user_id && Double.compare(balance, account.balance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(account_id, user_id, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + account_id +
                ", user_id=" + user_id +
                ", balance=" + balance +
                '}';
    }
}
