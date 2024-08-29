package com.techelevator.tenmo.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

public class TransferStatus {
    @NotBlank
    private int transfer_status_id;
    @NotBlank
    private String transfer_status_desc;

    public TransferStatus(){

    }

    public TransferStatus(int transfer_status_id, String transfer_status_desc) {
        this.transfer_status_id = transfer_status_id;
        this.transfer_status_desc = transfer_status_desc;
    }

    public int getTransfer_status_id() {
        return transfer_status_id;
    }

    public void setTransfer_status_id(int transfer_status_id) {
        this.transfer_status_id = transfer_status_id;
    }

    public String getTransfer_status_desc() {
        return transfer_status_desc;
    }

    public void setTransfer_status_desc(String transfer_status_desc) {
        this.transfer_status_desc = transfer_status_desc;
    }
}
