package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
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
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);

        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    public List<Account> listUser() {
        List<Account> account = new ArrayList<>();
        String sql = "SELECT * from account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while (results.next()) {
            account.add(mapRowToAccount(results));
        }
        return account;

    }

    public void transferBalance(int sender, int receiver, double amountToAdd){
        String sql = "BEGIN TRANSACTION; UPDATE account SET balance = balance + ? WHERE account_id = ?; UPDATE account SET balance = balance - ? WHERE account_id = ?; COMMIT;";
        jdbcTemplate.update(sql, amountToAdd, receiver, amountToAdd, sender);
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account Account = new Account();
        Account.setUser_id(rs.getInt("user_id"));
        Account.setAccount_id(rs.getInt("account_id"));
        Account.setBalance(rs.getDouble("balance"));
        return Account;
    }
}
