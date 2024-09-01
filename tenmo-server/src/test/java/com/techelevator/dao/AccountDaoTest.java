package com.techelevator.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcAccessor;

public class AccountDaoTest extends BaseDaoTests {
    protected static final Account ACCOUNT_1 = new Account(2001, 1001, 1000.00);
    protected static final Account ACCOUNT_2 = new Account(2002, 1002, 1000.00);

    private AccountDao sut;
    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new AccountDao(jdbcTemplate);
    }

    @Test(expected = Exception.class)
    public void getAccountByUserId_given_invalid_id_throws_exception(){
     }

    @Test
    public void getAccountByUserId_given_valid_id_returns_user(){
    }

    @Test
    public void transferBalance_adjusts_balances(){
    }

    @Test(expected=Exception.class)
    public void transferBalance_throws_exception(){
    }
}
