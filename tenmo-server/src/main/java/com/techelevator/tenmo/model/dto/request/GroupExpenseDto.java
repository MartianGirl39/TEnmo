package com.techelevator.tenmo.model.dto.request;

import com.techelevator.tenmo.model.dto.response.AccountDto;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.util.Optional;

public class GroupExpenseDto {
    private String name;
    private String description;
    private double total;
    private LocalDate due_date;
    private boolean repeating;
    private SeriesDto series;
    int account_owed;

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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDate getDue_date() {
        return due_date;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public SeriesDto getSeries() {
        return series;
    }

    public void setSeries(SeriesDto series) {
        this.series = series;
    }

    public int getAccount_owed() {
        return account_owed;
    }

    public void setAccount_owed(int account_owed) {
        this.account_owed = account_owed;
    }
}
