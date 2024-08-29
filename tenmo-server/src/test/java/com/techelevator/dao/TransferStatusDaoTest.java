package com.techelevator.dao;

import com.techelevator.tenmo.dao.TransferStatusDao;
import com.techelevator.tenmo.model.TransferStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransferStatusDaoTest extends BaseDaoTests {
    private final TransferStatus STATUS_1 = new TransferStatus(1, "Pending");
    private final TransferStatus STATUS_2 = new TransferStatus(2, "Approved");
    private final TransferStatus STATUS_3 = new TransferStatus(3, "Rejected");

    private TransferStatusDao transferStatusDao;
    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        transferStatusDao = new TransferStatusDao(jdbcTemplate);
    }

    // TODO: test we can retrieve TransferStatus by id
    @Test
    public void getTransferStatusByIdTest(){

    }

    @Test
    public void getTransferStatusByIdTest_returns_null_on_invalid_id(){

    }

    // TODO: ensure we can retrieve TransferStatus by desc
    @Test
    public void getTransferStatusByDescTest(){

    }

    @Test
    public void getTransferStatusByDescTest_returns_null_on_invalid_id(){

    }
}
