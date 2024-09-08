package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Series;
import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;
import com.techelevator.tenmo.model.dto.request.SeriesDto;
import com.techelevator.tenmo.model.dto.response.AccountDto;
import com.techelevator.tenmo.model.dto.response.ClientGroupExpenseDto;
import com.techelevator.tenmo.model.dto.response.ClientGroupMemberDto;
import com.techelevator.tenmo.model.dto.response.ContributionDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ExpenseDao {

    JdbcTemplate jdbcTemplate;

    public ExpenseDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ClientGroupExpenseDto getGroupExpense(int expenseId){
        ClientGroupExpenseDto expense = null;
        String sql = "SELECT expense_id, name, description, total_needed, due_date, repeating, expense_status_desc, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM group_expense JOIN expense_status ON expense_status.expense_status_id = group_expense.expense_status_id JOIN account ON receiving_account = account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, expenseId);
        if(results.next()){
            expense = this.mapRowToExpense(results);
        }
        return expense;
    }

    public GroupExpenseDto getGroupExpenseAsGroupExpenseDto(int expenseId){
        GroupExpenseDto expense = null;
        String sql = "SELECT name, description, total_needed, due_date, repeating, expense_status_desc, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name, frequency_in_days, end_date FROM group_expense JOIN expense_status ON expense_status.expense_status_id = group_expense.expense_status_id JOIN group_expense_series ON group_expense_series.series_id = group_expense.series_id JOIN account ON receiving_account = account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, expenseId);
        if(results.next()){
            expense = this.mapRowToGroupExpenseDto(results);
        }
        return expense;
    }

    public List<ClientGroupExpenseDto> getGroupExpenses(int id){
        List<ClientGroupExpenseDto> expenses = new ArrayList<>();
        String sql = "SELECT expense_id, name, description, total_needed, due_date, repeating, expense_status_desc, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM group_expense JOIN expense_status ON expense_status.expense_status_id = group_expense.expense_status_id JOIN account ON receiving_account = account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE group_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            ClientGroupExpenseDto expense = this.mapRowToExpense(results);
            expenses.add(this.mapRowToExpense(results));
        }
        return expenses;
    }

    @Transactional
    public ClientGroupExpenseDto addExpenseToGroup(int groupId, GroupExpenseDto data){
        ClientGroupExpenseDto expense = null;
        int expenseId = 0, seriesId = 1;
        if(data.isRepeating()){
            seriesId = this.createSeries(data.getSeries());
            System.out.println(seriesId);
        }
        String sql = "INSERT INTO group_expense (group_id, total_needed, due_date, expense_status_id, repeating, receiving_account, series_id, name, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING expense_id;";
        expenseId = jdbcTemplate.queryForObject(sql, int.class, groupId, data.getTotal(), data.getDue_date(), 1, data.isRepeating(), data.getAccount_owed(), seriesId, data.getName(), data.getDescription());
        expense = this.getGroupExpense(expenseId);
        return expense;
    }

    // update basics
    @Transactional
    public ClientGroupExpenseDto updateGroupExpense(int expenseId, GroupExpenseDto groupExpenseDto){
        ClientGroupExpenseDto expense = null;
        // check if is repeating used to be true, but is now false, if so, delete old series
        GroupExpenseDto current = this.getGroupExpenseAsGroupExpenseDto(expenseId);
        if(current.isRepeating() && !groupExpenseDto.isRepeating()){
            Series toRemove = findSeriesByExpense(expenseId);
            this.deleteSeries(toRemove.getSeriesId());
        }
        else if(groupExpenseDto.isRepeating()){
            this.updateGroupExpenseSeries(expenseId, groupExpenseDto.getSeries());
        }
        String sql = "UPDATE group_expense SET name = ?, description = ?, total_needed = ?, due_date = ?, receiving_account = ?, repeating = ? WHERE expense_id = ?";
        jdbcTemplate.update(sql, groupExpenseDto.getName(), groupExpenseDto.getDescription(), groupExpenseDto.getTotal(), groupExpenseDto.getDue_date(), groupExpenseDto.getAccount_owed(), groupExpenseDto.isRepeating(), expenseId);
        expense = this.getGroupExpense(expenseId);
        return expense;
    }

    // update series
    // check if series exists, add a new one, and then delete the old one
    @Transactional
    public ClientGroupExpenseDto updateGroupExpenseSeries(int expenseId, SeriesDto series){
        ClientGroupExpenseDto expense = null;
        GroupExpenseDto currentExpense = this.getGroupExpenseAsGroupExpenseDto(expenseId);
        String sql = "";
        if(currentExpense == null){
            return null;
        }
        if(!currentExpense.isRepeating()) {
            sql =  "UPDATE group_expense SET repeating = ?, series_id = ? WHERE expense_id = ?";
            int newSeriesId = this.createSeries(series);
            jdbcTemplate.update(sql, true, newSeriesId, expenseId);
        }
        else {
            Series toUpdate = this.findSeriesByExpense(expenseId);
            System.out.println(toUpdate);
            if(toUpdate != null) {
                sql = "UPDATE group_expense_series SET frequency_in_days = ?, end_date = ? WHERE series_id = ?";
                jdbcTemplate.update(sql, series.getFrequency(), series.getEndDate(), toUpdate.getSeriesId());
            }
            else {
                return null;
            }
        }
        return this.getGroupExpense(expenseId);
    }

    public ClientGroupExpenseDto updateGroupExpenseStatus(int expenseId, int statusId){
        ClientGroupExpenseDto expense = null;
        String sql = "UPDATE group_expense SET expense_status_id = ? WHERE expense_id = ?";
        jdbcTemplate.update(sql, statusId, expenseId);
        expense = this.getGroupExpense(expenseId);
        return expense;
    }

    public void deleteGroupExpense(int expense){
        String sql = "DELETE FROM group_expense WHERE expense_id = ?";
        jdbcTemplate.update(sql, expense);
    }

    public void linkTransfer(int transferId, int expenseId){
        String sql = "INSERT INTO expense_transfer (expense_id, transfer_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, expenseId, transferId);
    }

    public ContributionDto getExpenseContribution(int expenseId, int memberId){
        ContributionDto contribution = null;
        String sql = "SELECT group_expense_contribution.amount, member_role, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM group_expense_contribution JOIN account ON group_expense_contribution.account_id = account.account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id JOIN friend_group_member ON friend_group_member.member_id = group_expense_contribution.account_id WHERE expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, expenseId, memberId);
        if(results.next()){
            contribution = this.mapRowToContribution(results);
        }
        return contribution;
    }

    public List<ContributionDto> getExpenseContributions(int id){
        List<ContributionDto> contributions = new ArrayList<>();
        String sql = "SELECT group_expense_contribution.amount, member_role, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM group_expense_contribution JOIN account ON group_expense_contribution.account_id = account.account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id JOIN friend_group_member ON friend_group_member.member_id = group_expense_contribution.account_id WHERE expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            contributions.add(this.mapRowToContribution(results));
        }
        return contributions;
    }

    public ContributionDto addNewContributionToExpense(int expenseId, int memberId, double amount){
        ContributionDto contribution = null;
        String sql = "INSERT INTO group_expense_contribution (expense_id, account_id, amount) VALUES (?, ?, ?)";
        int index = jdbcTemplate.update(sql, expenseId, memberId, amount);
        return this.getExpenseContribution(expenseId, memberId);
    }

    public ContributionDto addToExistingContribution(int expenseId, int memberId, double amount){
        ContributionDto contribution = null;
        String sql = "UPDATE group_expense_contribution SET amount = amount + ? WHERE expense_id = ? AND account_id = ?";
        jdbcTemplate.update(sql, amount, expenseId, memberId);
        return this.getExpenseContribution(expenseId, memberId);
    }

    public ContributionDto removeFromContribution(int expenseId, int memberId, double amount){
        ContributionDto contribution = null;
        String sql = "UPDATE group_expense_contribution SET amount = amount - ? WHERE expense_id = ? AND member_id = ?";
        jdbcTemplate.update(sql, amount, expenseId, memberId);
        return this.getExpenseContribution(expenseId, memberId);
    }

    public void deleteContribution(int expenseId, int memberId){
        String sql = "DELETE FROM group_expense_contribution WHERE expense_id = ?, member_id = ?";
        jdbcTemplate.update(sql, expenseId, memberId);
    }

    private Series findSeriesByExpense(int expenseId){
        Series series = null;
        String sql = "SELECT * FROM group_expense_series JOIN group_expense ON group_expense.series_id = group_expense_series.series_id WHERE group_expense.expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, expenseId);
        if(results.next()){
            series = mapRowToSeries(results);
        }
        return series;
    }

    private int createSeries(SeriesDto series){
        String sql = "INSERT INTO group_expense_series (frequency_in_days, end_date) VALUES (?, ?) RETURNING series_id";
        return jdbcTemplate.queryForObject(sql, int.class, series.getFrequency(), series.getEndDate());
    }

    @Transactional
    private void deleteSeries(int seriesId){
        String sql = "BEGIN TRANSACTION; UPDATE group_expense SET series_id = 1 WHERE series_id = ?; DELETE FROM group_expense_series WHERE series_id = ?; COMMIT;";
        jdbcTemplate.update(sql, seriesId, seriesId);
    }

    private Series mapRowToSeries(SqlRowSet rs){
        Series series = new Series();
        series.setSeriesId(rs.getInt("series_id"));
        series.setFrequency(rs.getInt("frequency_in_days"));
        Date date = rs.getDate("end_date");
        if(date != null){
            series.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        return series;
    }

    private ClientGroupExpenseDto mapRowToExpense(SqlRowSet rs){
        ClientGroupExpenseDto expense = new ClientGroupExpenseDto();
        expense.setExpense_id(rs.getInt("expense_id"));
        expense.setName(rs.getString("name"));
        expense.setDescription(rs.getString("description"));
        expense.setTotal(rs.getDouble("total_needed"));
        expense.setDue_date(rs.getDate("due_date").toLocalDate());
        expense.setStatus(rs.getString("expense_status_desc"));
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        expense.setAccount(account);
        return expense;
    }

    private GroupExpenseDto mapRowToGroupExpenseDto(SqlRowSet rs){
        GroupExpenseDto expense = new GroupExpenseDto();
        expense.setName(rs.getString("name"));
        expense.setDescription(rs.getString("description"));
        expense.setTotal(rs.getDouble("total_needed"));
        expense.setDue_date(rs.getDate("due_date").toLocalDate());
        expense.setRepeating(rs.getBoolean("repeating"));
        expense.setAccount_owed(rs.getInt("account_id"));
        SeriesDto series = new SeriesDto();
        series.setFrequency(rs.getInt("frequency_in_days"));
        Date date = rs.getDate("end_date");
        if(date != null){
            series.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        expense.setSeries(series);
        return expense;
    }

    private ContributionDto mapRowToContribution(SqlRowSet rs){
        ContributionDto contribution = new ContributionDto();
        contribution.setAmount(rs.getDouble("amount"));
        ClientGroupMemberDto member = new ClientGroupMemberDto();
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        member.setMember_role(rs.getString("member_role"));
        member.setAccount(account);
        contribution.setAccount(member);
        return contribution;
    }
}
