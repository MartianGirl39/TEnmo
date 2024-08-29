package com.techelevator.tenmo.model;

public class TransferStatusDto {
    int id;
    // account id that's sending it
    int sendingAccount;
    //status code of transfer
    String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSendingAccount() {
        return sendingAccount;
    }

    public void setSendingAccount(int sendingAccount) {
        this.sendingAccount = sendingAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
