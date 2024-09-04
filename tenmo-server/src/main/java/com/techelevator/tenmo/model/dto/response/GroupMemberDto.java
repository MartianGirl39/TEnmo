package com.techelevator.tenmo.model.dto.response;

import com.techelevator.tenmo.model.dto.response.AccountDto;

public class GroupMemberDto {
    AccountDto account;
    String member_role;

    public GroupMemberDto(){

    }

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }

    public String getMember_role() {
        return member_role;
    }

    public void setMember_role(String member_role) {
        this.member_role = member_role;
    }
}
