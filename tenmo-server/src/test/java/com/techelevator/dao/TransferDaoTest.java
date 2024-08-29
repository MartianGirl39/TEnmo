package com.techelevator.dao;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransferDaoTest extends BaseDaoTests{

    private static final Transfer PENDING_TRANSFER_1 = new Transfer(3001, 1, 1, 2001, 2002, 100);
    private static final Transfer PENDING_TRANSFER_2 = new Transfer(3002, 1, 1, 2003, 2001, 50);
    private static final Transfer PENDING_TRANSFER_3 = new Transfer(3003, 1, 1, 2003, 2002, 200);
    private static final Transfer APPROVED_TRANSFER_1 = new Transfer(3004, 2, 2, 2001, 2002, 70);
    private static final Transfer APPROVED_TRANSFER_2 = new Transfer(3005, 2, 2, 2002, 2003, 20);
    private static final Transfer APPROVED_TRANSFER_3 = new Transfer(3006, 2, 2, 2003, 2002, 300);
    private static final Transfer REJECTED_TRANSFER_1 = new Transfer(3007, 1, 3, 2001, 2002, 110);
    private static final Transfer REJECTED_TRANSFER_2 = new Transfer(3008, 1, 3, 2003, 2001, 60);
    private static final Transfer REJECTED_TRANSFER_3 = new Transfer(3009, 1, 3, 2003, 2002, 150);

    private TransferDao sut;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new TransferDao(jdbcTemplate);
    }

    // TODO: test get transfer by id gets an appropriate transfer


    // TODO: test get transfers by user retrieves the right transfers for each user


    // TODO: test that changing transfer type from pending to approved works


    // TODO: test that changing transfer type from pending to rejected works


    // TODO: test that changing transfer type from Approved or Rejected throws error


    // TODO: test get transfer by status returns the correct transfers for every possible status


    // TODO: test that send sendTEBucks returns the correct transfer


    // TODO: test that requestTEBucks creates the correct transfer


    // TODO: test delete transfer can only delete a pending transfer of type request
}