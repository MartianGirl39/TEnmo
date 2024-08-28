package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class TransferStatusDao {
    private JdbcTemplate jdbcTemplate;

    TransferStatusDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TransferStatus getStatusById(int id) {

        String sql = "SELECT * FROM transfer_status WHERE transfer_status_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        return null;
    }

    public TransferStatus getStatusByName(String name) {

        String sql = "SELECT * FROM transfer_status WHERE transfer_status_desc = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        return null;
    }

    public TransferStatus mapRowToTransfer(SqlRowSet results) {
        TransferStatus status = new TransferStatus();
        status.setTransfer_status_id(results.getInt("transfer_status_id"));
        status.setTransfer_status_desc(results.getString("transfer_status_desc"));
        return status;
    }
}
