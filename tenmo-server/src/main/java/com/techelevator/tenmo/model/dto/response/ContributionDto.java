package com.techelevator.tenmo.model.dto.response;

import com.techelevator.tenmo.model.dto.response.AccountDto;

public class ContributionDto {
    AccountDto account;
    double amount;

    public ContributionDto(){

    }

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
