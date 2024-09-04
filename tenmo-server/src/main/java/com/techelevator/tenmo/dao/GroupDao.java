package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.dto.request.GroupDto;
import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;
import com.techelevator.tenmo.model.dto.response.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GroupDao {

    private AccountDao accountDao;
    private JdbcTemplate jdbcTemplate;

    GroupDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ClientGroupDto getGroupById(int id){
        ClientGroupDto group = null;
        String sql = "SELECT group_id, group_name, description FROM friend_group WHERE group_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if(results.next()){
                group = mapRowToGroup(results);
            }
        }
        catch(DataAccessException err){
            throw new DaoException();
        }
        return group;
    }

    public List<ClientGroupDto> getGroupByUser(int id){
        List<ClientGroupDto> groups = new ArrayList<>();
        String sql = "SELECT group_id, group_name, description FROM friend_group JOIN friend_group_member ON friend_group.group_id = friend_group_member.group_id WHERE friend_group_member.account_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            groups.add(this.mapRowToGroup(results));
        }
        return groups;
    }

    public ClientGroupDto createGroup(int creatorId, GroupDto groupDto){
        ClientGroupDto newGroup = null;
        int groupId = -1;
        String sql = "INSERT INTO friend_group (group_name, description, creator_id, date_created) VALUES (?, ?, ?) RETURNING group_id";
        groupId = jdbcTemplate.queryForObject(sql, int.class, groupDto.getName(), groupDto.getDescription(), creatorId, LocalDate.now());
        newGroup = this.getGroupById(groupId);
        if (newGroup != null){
            GroupMemberDto creator = new GroupMemberDto();
            creator.setMember_role("CREATOR");
            creator.setAccount(accountDao.getAccountDtoById(creatorId));
            this.addMemberToGroup(groupId, creator);
        }
        return newGroup;
    }

    public ClientGroupDto updateGroup(int groupId, GroupDto groupDto){
        String sql = "UPDATE friend_group SET name = ?, description = ? WHERE group_id = ?";
        jdbcTemplate.update(sql, groupDto.getName(), groupDto.getDescription());
        return this.getGroupById(groupId);
    }

    public void deleteGroup(int groupId){
        String sql = "DELETE FROM friend_group WHERE group_id = ?";
        jdbcTemplate.update(sql, groupId);
    }

    public GroupMemberDto getGroupMember(int groupId, int memberId){
        GroupMemberDto member = null;
        String sql = "SELECT friend_group_member.member_role, account_id, username, first_name, last_name FROM friend_group_member JOIN account ON account_id = member_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE group_id = ? AND member_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, groupId, memberId);
        if(results.next()){
            member = this.mapRowToMembers(results);
        }
        return member;
    }

    public List<GroupMemberDto> getGroupMembers(int id){
        List<GroupMemberDto> members = new ArrayList<>();
        String sql = "SELECT friend_group_member.member_role, account_id, username, first_name, last_name FROM friend_group_member JOIN account ON account_id = member_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE group_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            members.add(this.mapRowToMembers(results));
        }
        return members;
    }

    public GroupMemberDto addMemberToGroup(int id, GroupMemberDto groupMemberDto){
        GroupMemberDto newMember = null;
        String sql = "INSERT INTO friend_group_member (group_id, member_id, member_role) VALUES (?, ?, ?) RETURNING group_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, int.class, id, groupMemberDto.getAccount().getAccount_id(), groupMemberDto.getMember_role());
        if(results.next()){
            newMember = this.getGroupMember(results.getInt("group_id"), results.getInt("member_id"));
        }
        return newMember;
    }

    public void removeMemberFromGroup(int id, GroupMemberDto groupMemberDto){
        String sql = "DELETE FROM friend_group_member WHERE group_id = ? AND member_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public ClientGroupExpenseDto getGroupExpense(int expenseId){
        ClientGroupExpenseDto expense = null;
        String sql = "SELECT total, due-date, repeating, transfer_status_desc FROM group_expense JOIN transfer_status ON transfer_status.transfer_status_id = group_expense.transfer_status_id WHERE expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, expenseId);
        if(results.next()){
            expense = this.mapRowToExpense(results);
        }
        return expense;
    }

    public List<ClientGroupExpenseDto> getGroupExpenses(int id){
        List<ClientGroupExpenseDto> expenses = new ArrayList<>();
        String sql = "SELECT total, due-date, repeating, transfer_status_desc FROM group_expense JOIN transfer_status ON transfer_status.transfer_status_id = group_expense.transfer_status_id WHERE group_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            ClientGroupExpenseDto expense = this.mapRowToExpense(results);
            expenses.add(this.mapRowToExpense(results));
        }
        return expenses;
    }

    public ClientGroupExpenseDto addExpenseToGroup(int groupId, GroupExpenseDto groupExpenseDto){
        ClientGroupExpenseDto expense = null;
        int expenseId = -1;
        String sql = "INSERT INTO group_expense (group_id, total_needed, name, description, due_date, repeating, expense_status) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING expense_id";
        expenseId = jdbcTemplate.queryForObject(sql, int.class, groupId, groupExpenseDto.getTotalNeeded(), groupExpenseDto.getName(), groupExpenseDto.getDescription(), groupExpenseDto.getDate(), groupExpenseDto.isRepeating(), 1);
        expense = this.getGroupExpense(expenseId);
        return expense;
    }

    public ClientGroupExpenseDto updateGroupExpense(int expenseId, GroupExpenseDto groupExpenseDto){
        ClientGroupExpenseDto expense = null;
        String sql = "UPDATE group_expense SET name = ?, description = ?, total_needed = ?, due_date = ?, repeating = ? WHERE expense_id = ?";
        jdbcTemplate.update(sql, groupExpenseDto.getName(), groupExpenseDto.getDescription(), groupExpenseDto.getTotalNeeded(), groupExpenseDto.getDate(), groupExpenseDto.isRepeating(), expenseId);
        expense = this.getGroupExpense(expenseId);
        return expense;
    }

    public void deleteGroupExpense(int expense){
        String sql = "DELETE FROM group_expense WHERE expense_id = ?";
        jdbcTemplate.update(sql, expense);
    }

    public void settleExpense(int expenseId){

    }

    public ContributionDto getExpenseContribution(int expenseId, int memberId){
        ContributionDto contribution = null;
        String sql = "SELECT group_expense_contribution.amount, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM group_expense_contribution JOIN account ON group_expense_contribution.account_id = account.account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE expense_id = ? AND group_member_contribution.account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, expenseId, memberId);
        if(results.next()){
            contribution = this.mapRowToContribution(results);
        }
        return contribution;
    }

    public List<ContributionDto> getExpenseContributions(int id){
        List<ContributionDto> contributions = new ArrayList<>();
        String sql = "SELECT group_expense_contribution.amount, account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM group_expense_contribution JOIN account ON group_expense_contribution.account_id = account.account_id JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE expense_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            contributions.add(this.mapRowToContribution(results));
        }
        return contributions;
    }

    public ContributionDto addToContribution(int expenseId, int memberId, double amount){
        ContributionDto contribution = null;
        String sql = "UPDATE group_expense_contribution SET amount = amount + ? WHERE expense_id = ? AND member_id = ?";
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

    private ClientGroupDto mapRowToGroup(SqlRowSet rs){
        ClientGroupDto group = new ClientGroupDto();
        group.setGroup_id(rs.getInt("group_id"));
        group.setGroup_name(rs.getString("group_name"));
        group.setDescription(rs.getString("description"));
        return group;
    }

    private GroupMemberDto mapRowToMembers(SqlRowSet rs){
        GroupMemberDto member = new GroupMemberDto();
        member.setMember_role(rs.getString("member_role"));
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        member.setAccount(account);
        return member;
    }

    private ClientGroupExpenseDto mapRowToExpense(SqlRowSet rs){
        ClientGroupExpenseDto expense = new ClientGroupExpenseDto();;
        expense.setTotalNeeded(rs.getDouble("total_needed"));
        expense.setTotalGiven(rs.getDouble("total_given"));
        expense.setDate(rs.getDate("due_date").toLocalDate());
        expense.setRepeating(rs.getBoolean("repeating"));
        expense.setStatus(rs.getString("transfer_status_desc"));
        return expense;
    }

    private ContributionDto mapRowToContribution(SqlRowSet rs){
        ContributionDto contribution = new ContributionDto();
        contribution.setAmount(rs.getDouble("total"));
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        return contribution;
    }
}
