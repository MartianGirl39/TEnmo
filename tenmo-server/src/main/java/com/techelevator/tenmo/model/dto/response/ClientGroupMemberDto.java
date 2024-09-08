package com.techelevator.tenmo.model.dto.response;

public class ClientGroupMemberDto {
    AccountDto account;
    String member_role;

    public ClientGroupMemberDto(){

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
