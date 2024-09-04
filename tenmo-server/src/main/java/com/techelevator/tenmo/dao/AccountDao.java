package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.dto.response.UserAccountDto;
import org.springframework.dao.DataAccessException;
import com.techelevator.tenmo.model.dto.response.AccountDto;
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

    public UserAccountDto getAccountByUserId(int id) {
        UserAccountDto account = null;
        String sql = "SELECT account_id, balance, username, tenmo_user.first_name, tenmo_user.last_name FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account.user_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                account = mapRowToUserAccount(results);
            }
            return account;
        }
        catch (DataAccessException err){
            System.out.println(err.getMessage());
            throw new DaoException();
        }
    }

    public Account getAccountById(int id) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
            return account;
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    public List<Account> listUser(int account_id) {
        // creates a list of accounts
        List<Account> account = new ArrayList<>();
        // select every account from account
        String sql = "SELECT * FROM account LEFT JOIN friend ON user_1 = account_id OR user_2 = account_id WHERE account_id != 2001 AND (user_1 = 2001 OR user_2 = 2001)";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
            while (results.next()) {
                account.add(mapRowToAccount(results));
            }
            return account;
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
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

    public void addToBalance(int accountNumber, double amountToAdd){
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
        try {
            jdbcTemplate.update(sql, amountToAdd, accountNumber);
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    public void removeFromBalance(int accountNumber, double amountToAdd){
        String sql = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        try {
            jdbcTemplate.update(sql, amountToAdd, accountNumber);
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
        String sql = "SELECT account.account_id, account.balance, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account.user_id = ?;";
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
        String sql = "SELECT account.account_id, account.balance, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account_id = ?;";
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

        String sql = "SELECT account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
            if (results.next()) {
                account = mapRowToAccountDto(results);
            }
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
        return account;
    }

    public AccountDto getAccountDtoByUsername(String username){
        AccountDto account = null;

        String sql = "SELECT account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE username = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            if (results.next()) {
                account = mapRowToAccountDto(results);
            }
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
        return account;
    }

    public List<AccountDto> getAccounts(int userAccount){
        List<AccountDto> account = new ArrayList<>();
        // select every account from account
        String sql = "SELECT account.account_id, tenmo_user.username, tenmo_user.first_name, tenmo_user.last_name FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id LEFT JOIN friend ON user_1 = account_id OR user_2 = account_id WHERE account_id != ? AND (user_1 = ? OR user_2 = ?)";
//        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userAccount, userAccount, userAccount);
            while (results.next()) {
                account.add(mapRowToAccountDto(results));
            }
//        }
//        catch (DataAccessException err) {
//            throw new DaoException();
//        }
        return account;
    }

    public void createFriendship(int user1, int user2){
        String sql = "INSERT INTO friend (user_1, user_2) " +
                "SELECT ?, ?  " +
                "WHERE NOT EXISTS (  " +
                "    SELECT * FROM friend " +
                "    WHERE (user_1 = ? AND user_2 = ?) OR (user_1 = ? AND user_2 = ?)" +
                ");";
        jdbcTemplate.update(sql, user1, user2, user1, user2, user2, user1);
    }

    private AccountDto mapRowToAccountDto(SqlRowSet rs){
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        return account;
    }

    private UserAccountDto mapRowToUserAccount(SqlRowSet rs) {
        UserAccountDto account = new UserAccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setBalance(rs.getDouble("balance"));
        account.setUsername(rs.getString("username"));
        account.setFirst_name(rs.getString("first_name"));
        account.setLast_name(rs.getString("last_name"));
        return account;
    }
}
