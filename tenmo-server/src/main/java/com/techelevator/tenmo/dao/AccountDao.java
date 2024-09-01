package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
<<<<<<< HEAD
import com.techelevator.tenmo.model.dto.UserAccountDto;
import org.springframework.dao.DataAccessException;
=======
import com.techelevator.tenmo.model.dto.AccountDto;
import com.techelevator.tenmo.model.dto.UserAccountDto;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
>>>>>>> 48f84f9a3330be474b67539f14c83ee44aa62401
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.net.UnknownServiceException;
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
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
<<<<<<< HEAD
            return account;
=======
>>>>>>> 48f84f9a3330be474b67539f14c83ee44aa62401
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    public Account getAccountById(int id) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
<<<<<<< HEAD

            if (results.next()) {
                account = mapRowToAccount(results);
            }
            return account;
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
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
=======
            if (results.next()) {
                account = mapRowToAccount(results);
            }
>>>>>>> 48f84f9a3330be474b67539f14c83ee44aa62401
        }
        catch (DataAccessException err){
            throw new DaoException();
        }
    }

    public List<Account> listUser(int account_id) {
        // creates a list of accounts
        List<Account> account = new ArrayList<>();
        // select every account from account
        String sql = "SELECT * FROM account WHERE account_id != ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
            while (results.next()) {
                account.add(mapRowToAccount(results));
            }
<<<<<<< HEAD
            return account;
        }
        catch (DataAccessException err) {
=======
        }
        catch (DataAccessException err){
>>>>>>> 48f84f9a3330be474b67539f14c83ee44aa62401
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

    private Account mapRowToAccount(SqlRowSet rs) {
        Account Account = new Account();
        Account.setUser_id(rs.getInt("user_id"));
        Account.setAccount_id(rs.getInt("account_id"));
        Account.setBalance(rs.getDouble("balance"));
        return Account;
    }

<<<<<<< HEAD
=======
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

        String sql = "SELECT account.account_id, tenmo_user.username FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE username = ?";
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

    public List<AccountDto> getAccount(int userAccount){
        List<AccountDto> account = new ArrayList<>();
        // select every account from account
        String sql = "SELECT account.account_id, tenmo_user.username FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE account_id != ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userAccount);
            while (results.next()) {
                account.add(mapRowToAccountDto(results));
            }
        }
        catch (DataAccessException err) {
            throw new DaoException();
        }
        return account;
    }

    private AccountDto mapRowToAccountDto(SqlRowSet rs){
        AccountDto account = new AccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setUsername(rs.getString("username"));
        return account;
    }

>>>>>>> 48f84f9a3330be474b67539f14c83ee44aa62401
    private UserAccountDto mapRowToUserAccount(SqlRowSet rs) {
        UserAccountDto account = new UserAccountDto();
        account.setAccount_id(rs.getInt("account_id"));
        account.setBalance(rs.getDouble("balance"));
        account.setUsername(rs.getString("username"));
        return account;
    }
}
