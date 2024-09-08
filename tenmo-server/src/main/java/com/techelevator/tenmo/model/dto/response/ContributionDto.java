package com.techelevator.tenmo.model.dto.response;

import com.techelevator.tenmo.model.dto.response.AccountDto;

import javax.validation.constraints.Min;

public class ContributionDto {
    ClientGroupMemberDto account;
    @Min(0)
    double amount;

    public ContributionDto(){

    }

    public ClientGroupMemberDto getAccount() {
        return account;
    }

    public void setAccount(ClientGroupMemberDto account) {
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
