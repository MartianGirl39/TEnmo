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
        sut.getAccountByUserId(-1);
    }

    @Test
    public void getAccountByUserId_given_valid_id_returns_user(){
        Assert.assertEquals(ACCOUNT_1, sut.getAccountByUserId(ACCOUNT_1.getUser_id()));
    }

    @Test
    public void transferBalance_adjusts_balances(){
        sut.transferBalance(ACCOUNT_1.getAccount_id(), ACCOUNT_2.getAccount_id(), 100);
        Account sender = sut.getAccountByUserId(ACCOUNT_1.getUser_id());
        Account receiver = sut.getAccountByUserId(ACCOUNT_2.getUser_id());
        Assert.assertEquals(ACCOUNT_1.getBalance() - 100, sender.getBalance(),15);
        Assert.assertEquals(ACCOUNT_2.getBalance() + 100, receiver.getBalance(), 15);
    }

    @Test(expected=Exception.class)
    public void transferBalance_throws_exception(){
        sut.transferBalance(ACCOUNT_1.getAccount_id(), ACCOUNT_2.getAccount_id(), 1001);
    }
}
