package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.dto.AccountDto;
import com.techelevator.tenmo.model.dto.UserAccountDto;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountDao {

    private JdbcTemplate jdbcTemplate;

    public AccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account getAccountByUserId(int id) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    public Account getAccountById(int id) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    public List<Account> listUser(int account_id) {
        // creates a list of accounts
        List<Account> account = new ArrayList<>();
        // select every account from account
        String sql = "SELECT * FROM account WHERE account_id != ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
        while (results.next()) {
            account.add(mapRowToAccount(results));
        }
        return account;
    }

    public void transferBalance(int sender, int receiver, double amountToAdd){
        String sql = "BEGIN TRANSACTION; UPDATE account SET balance = balance + ? WHERE account_id = ?; UPDATE account SET balance = balance - ? WHERE account_id = ?; COMMIT;";
        try {
            jdbcTemplate.update(sql, amountToAdd, receiver, amountToAdd, sender);
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account Account = new Account();
        Account.setUser_id(rs.getInt("user_id"));
        Account.setAccount_id(rs.getInt("account_id"));
        Account.setBalance(rs.getDouble("balance"));
        return Account;
    }

    public UserAccountDto getUserAccountByUserId(int id) {
        UserAccountDto account = null;
        String sql = "SELECT account.account_id, account.balance, tenmo_user.username FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account.user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                account = mapRowToUserAccount(results);
            }
            return account;
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    public UserAccountDto getUserAccountById(int id) {
        UserAccountDto account = null;
        String sql = "SELECT account.account_id, account.balance, tenmo_user.username FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

            if (results.next()) {
                account = mapRowToUserAccount(results);
            }
            return account;
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    public AccountDto getAccountDtoById(int account_id){
        AccountDto account = null;

        String sql = "SELECT account.account_id, tenmo_user.username FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
        if(results.next()){
            account = mapRowToAccountDto(results);
        }
        return account;
    }

    public List<AccountDto> getAccount(int userAccount){
        List<AccountDto> account = new ArrayList<>();
        // select every account from account
        String sql = "SELECT account.account_id, tenmo_user.username FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account_id != ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userAccount);
        while (results.next()) {
            account.add(mapRowToAccountDto(results));
        }
        return account;
    }

    private AccountDto mapRowToAccountDto(SqlRowSet rs){
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        return account;
    }

    private UserAccountDto mapRowToUserAccount(SqlRowSet rs) {
        UserAccountDto account = new UserAccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setBalance(rs.getDouble("balance"));
        account.setUsername(rs.getString("username"));
        return account;
    }
}
