package com.techelevator.tenmo.model.dto.response;

import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;

import java.time.LocalDate;

public class ClientGroupExpenseDto extends GroupExpenseDto {
    private double totalGiven;
    private String status;

    public ClientGroupExpenseDto(){

    }

    public double getTotalNeeded() {
        return super.getTotalNeeded();
    }

    public void setTotalNeeded(double totalNeeded) {
        super.setTotalNeeded(totalNeeded);
    }


    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public String getDescription() {
        return super.getDescription();
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }


    public double getTotalGiven() {
        return totalGiven;
    }

    public void setTotalGiven(double totalGiven) {
        this.totalGiven = totalGiven;
    }

    public LocalDate getDate() {
        return super.getDate();
    }

    public void setDate(LocalDate date) {
        super.setDate(date);
    }

    public boolean isRepeating() {
        return super.isRepeating();
    }

    public void setRepeating(boolean repeating) {
        super.setRepeating(repeating);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
