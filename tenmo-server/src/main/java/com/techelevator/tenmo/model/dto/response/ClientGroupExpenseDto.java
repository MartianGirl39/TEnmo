package com.techelevator.tenmo.model.dto.response;

import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;
import com.techelevator.tenmo.model.dto.request.SeriesDto;

import java.time.LocalDate;

public class ClientGroupExpenseDto {
    int expense_id;
    private String status;
    AccountDto account;
    private String name;
    private String description;
    private double total;
    private LocalDate due_date;

    public int getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(int expense_id) {
        this.expense_id = expense_id;
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

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
