package com.techelevator.tenmo.model.dto.response;

public class ClientTransferDto {
    private int transfer_id;
    private AccountDto sender;
    private AccountDto receiver;
    private String status;
    private String type;
    private double amount;
    private String message;

    public ClientTransferDto(){

    }
    public ClientTransferDto(int id, String type, String status, AccountDto sender, AccountDto receiver, double amount, String message) {
        this.transfer_id = id;
        this.type = type;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.message = message;
    }

    public int getTransfer_id() {
        return transfer_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTransfer_id(int transfer_id) {
        this.transfer_id = transfer_id;
    }

    public AccountDto getSender() {
        return sender;
    }

    public void setSender(AccountDto sender) {
        this.sender = sender;
    }

    public AccountDto getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountDto receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
