package com.techelevator.tenmo.model.dto.request;

import java.time.LocalDate;

public class GroupExpenseDto {
    private String name; //TODO: add to database
    private String description; //TODO: add to database
    private double totalNeeded;

    private LocalDate date;
    private boolean repeating;

    public GroupExpenseDto(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalNeeded() {
        return totalNeeded;
    }

    public void setTotalNeeded(double totalNeeded) {
        this.totalNeeded = totalNeeded;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
}
