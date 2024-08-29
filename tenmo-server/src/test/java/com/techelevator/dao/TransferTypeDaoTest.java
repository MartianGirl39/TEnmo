package com.techelevator.dao;

import com.techelevator.tenmo.dao.TransferTypeDao;
import com.techelevator.tenmo.model.TransferType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransferTypeDaoTest extends BaseDaoTests {

    private final TransferType TYPE_1 = new TransferType(1, "Request");
    private final TransferType TYPE_2 = new TransferType(2, "Send");

    private TransferTypeDao transferTypeDao;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        transferTypeDao = new TransferTypeDao(jdbcTemplate);
    }

    // TODO: test we can retrieve TransferType by id
    @Test
    public void getTransferTypeByIdTest(){

    }

    @Test
    public void getTransferTypeByIdTest_returns_null_on_invalid_id(){

    }

    // TODO: ensure we can retrieve TransferType by desc
    @Test
    public void getTransferTypeByDescTest(){

    }

    @Test
    public void getTransferTypeByDescTest_returns_null_on_invalid_id(){

    }
}
