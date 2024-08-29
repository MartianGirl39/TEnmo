package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TransferTypeDao {

    private JdbcTemplate jdbcTemplate;

    public TransferTypeDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public TransferType getTypeById(int id){
        String sql = "SELECT * FROM transfer_type WHERE transfer_type_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            return mapRowToTransferType(results);
        }
        return null;
    }

    public TransferType mapRowToTransferType(SqlRowSet results){
        TransferType type = new TransferType();
        type.setTransfer_type_id(results.getInt("transfer_type_id"));
        type.setTransfer_type_desc(results.getString("transfer_type_desc"));
        return type;

    }
}
