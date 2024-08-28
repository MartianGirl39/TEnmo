package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class TransferDao {
    private JdbcTemplate jdbcTemplate;

    public TransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Transfer getTransferById(int id) {
        Transfer transfer = new Transfer();
        String sql = "Select * from transfer where transfer_id = ? ;";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (result.next()) {
            transfer = mapRowToTransfer(result);
        }
        return transfer;
    }

    public List<Transfer> getTransferByUser(int user_id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = " select * from transfer where account_from = ? OR account_to = ? ;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id, user_id);

        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;

    }

    public List<Transfer> getTransfersByType(int user_id, String type ) {
        List<Transfer> transferTypeList = new ArrayList<>();

        String sql = " select transfer_id, transfer_type_id,transfer_status_id,account_from,account_to,amount from transfer t join transfer_type tt on tt.transfer_type_id = t.transfer_type_id where user_id = ? and transfer_type_desc = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id, type);

        while (results.next()) {
            transferTypeList.add(mapRowToTransfer(results));
        }
        return transferTypeList;


    }

    public int sendTeBucks(int senderId, int receiverId, double amount) {
        int id = 0;
        String sql = "insert into transfer (transfer_type_id,transfer_status_id,account_from,account_to,amount) values(2,2,?,?,?) returning transfer_id;";

        id = jdbcTemplate.queryForObject(sql, int.class, senderId, receiverId, amount);
        return id;
    }

    public int requestTeBucks(int senderId, int receiverId, double amount) {
        int id = 0;
        String sql = "insert into transfer (transfer_type_id,transfer_status_id,account_from,account_to,amount) values(1,1,?,?,?) returning transfer_id;";

        id = jdbcTemplate.queryForObject(sql, int.class, senderId, receiverId, amount);
        return id;
    }
    public void updateTransactionStatus(Transfer transfer){
        String sql = "update transfer"
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
