package com.techelevator.tenmo.model;

public class Transfer {

    private int transfer_id;
    private Account sender;
    private Account receiver;
    private String status;
    private String type;
    private double amount;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(int transfer_id) {
        this.transfer_id = transfer_id;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
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

    @Override
    public String toString() {
        return "ᆖᆖᆖTᆖrᆖaᆖnᆖsᆖfᆖeᆖrᆖᆖᆖ" +
                "\nTransfer Id: #" + this.transfer_id +
                "\nTransfer Type: " + this.type +
                "\n" + this.sender + " sends money to " + this.receiver +
                "\nTransfer Status: " + this.status +
                "\nTransfer Amount: " + this.amount +
                "\nMessage: ⚞" + this.message + "⚟\n"+
                "ᆖᆖᆖTᆖrᆖaᆖnᆖsᆖfᆖeᆖrᆖᆖᆖ\n\n";
    }
}
