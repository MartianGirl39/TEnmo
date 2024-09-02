package com.techelevator.tenmo.model.dto;

public class TransferStatusDto {
    // transfer id
    int id;
    // account id that's sending it
    //status code of transfer
    String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
