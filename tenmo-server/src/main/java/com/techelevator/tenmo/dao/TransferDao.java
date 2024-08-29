package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransferDao {
    private JdbcTemplate jdbcTemplate;

    public TransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sql = "Select * from transfer where transfer_id = ? ;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (result.next()) {
            transfer = mapRowToTransfer(result);
        }
        return transfer;
    }

    public List<Transfer> getTransferByUser(int user_id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = " select * from transfer where account_from = ? OR account_to = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id, user_id);

        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;

    }

    public List<Transfer> getTransfersByType(int user_id, String status_id) {
        List<Transfer> transferTypeList = new ArrayList<>();

        // SELECT * FROM transfer WHERE account_to = 2006 OR account_from = 2006 AND transfer_type_id = 1;

//        String sql = "SELECT * FROM transfer WHERE account_to = ? OR account_from = ? AND transfer_type_id = ?";
//
        String sql = "SELECT t.* FROM transfer t " +
                "JOIN account a ON t.account_from = a.account_id OR t.account_to = a.account_id " +
                "JOIN tenmo_user u ON a.user_id = u.user_id " +
                "JOIN transfer_status ts ON t.transfer_status_id = ts.transfer_status_id " +
                "WHERE account_id = ? AND ts.transfer_status_desc = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id, status_id);

        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transferTypeList.add(transfer);
        }

        return transferTypeList;
    }


    public int sendTeBucks(int senderId, int receiverId, double amount) {
        int id = 0;
        String sql = "insert into transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) values(2,2,?,?,?) returning transfer_id;";

        id = jdbcTemplate.queryForObject(sql, int.class, senderId, receiverId, amount);
        return id;
    }

    public int requestTeBucks(int senderId, int receiverId, double amount) {
        int id = 0;
        String sql = "insert into transfer (transfer_type_id,transfer_status_id,account_from,account_to,amount) values(1,1,?,?,?) returning transfer_id;";
        id = jdbcTemplate.queryForObject(sql, int.class, senderId, receiverId, amount);
        return id;
    }

    public void updateTransactionStatus(int id, int status_id) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, status_id, id);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer Transfer = new Transfer();
        Transfer.setTransfer_id(rs.getInt("transfer_id"));
        Transfer.setTransfer_type_id(rs.getInt("transfer_type_id"));
        Transfer.setTransfer_status_id(rs.getInt("transfer_status_id"));
        Transfer.setAccount_from(rs.getInt("account_from"));
        Transfer.setAccount_to(rs.getInt("account_to"));
        Transfer.setAmount(rs.getDouble("amount"));
        return Transfer;
    }
}
