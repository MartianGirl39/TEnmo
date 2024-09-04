package com.techelevator.dao;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.dto.response.AccountDto;
import com.techelevator.tenmo.model.dto.response.ClientTransferDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransferDaoTest extends BaseDaoTests{

//    INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, message) VALUES (1, 1, 2001, 2002, 100, "user1 to user2");
//    INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, message) VALUES (2, 2, 2002, 2001, 100, "user2 to user1");
//    INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, message) VALUES (1, 2, 2001, 2003, 100, "user1 to user3");
//    INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, message) VALUES (1, 3, 2003, 2001, 100, "user3 to user1");
//    INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount, message) VALUES (1, 4, 2003, 2002, 100, "user3 to user2");

    private static final ClientTransferDto TRANSFER_1 = new ClientTransferDto(3001, "Request", "Pending", new AccountDto(2001, "user1"), new AccountDto(2002, "user2"), 100, "user1 to user2");
    private TransferDao sut;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new TransferDao(jdbcTemplate);
    }

    @Test
    public void getTransferById_returns_null_on_invalid_id(){
        Assert.assertNull(sut.getClientTransferById(999));
    }

    @Test
    public void getTransferById_returns_right_transfer_on_valid_input(){
        Assert.assertEquals();
    }

    // TODO: test get transfers by user retrieves the right transfers for each user


    // TODO: test that changing transfer type from pending to approved works


    // TODO: test that changing transfer type from pending to rejected works


    // TODO: test that changing transfer type from Approved or Rejected throws error


    // TODO: test get transfer by status returns the correct transfers for every possible status


    // TODO: test that send sendTEBucks returns the correct transfer


    // TODO: test that requestTEBucks creates the correct transfer


    // TODO: test delete transfer can only delete a pending transfer of type request
}