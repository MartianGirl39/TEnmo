package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.dto.AccountDto;
import com.techelevator.tenmo.model.dto.ClientTransferDto;
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

    // git shows Jennifer+1, I started it and it failed. This is all Seth! He did awesome. I built upon his ideas and work for the ClientTransfer code :)
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

    public List<ClientTransferDto> getTransfers(int account_id){
        List<ClientTransferDto> transfers = new ArrayList<>();

        String sql = "SELECT " +
                "transfer.transfer_id, " +
                "transfer.account_to, " +
                "transfer.account_from, " +
                "transfer.amount, " +
                "transfer_status.transfer_status_desc, " +
                "transfer_type.transfer_type_desc, " +
                "user_to.username AS to, " +
                "user_from.username AS from " +
                "FROM transfer " +
                "JOIN  account AS account_from ON transfer.account_from = account_from.account_id " +
                "JOIN account AS account_to ON transfer.account_to = account_to.account_id " +
                "JOIN tenmo_user AS user_from ON account_from.user_id = user_from.user_id " +
                "JOIN tenmo_user AS user_to ON account_to.user_id = user_to.user_id " +
                "JOIN transfer_status ON transfer_status.transfer_status_id = transfer.transfer_status_id " +
                "JOIN transfer_type ON transfer_type.transfer_type_id = transfer.transfer_type_id " +
                "WHERE transfer.account_from = ? OR transfer.account_to = ?" +
                "ORDER BY transfer.transfer_id;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id, account_id);

        while(results.next()){
            transfers.add(mapRowToClientTransferObject(results));
        }

        return transfers;
    }

    public List<ClientTransferDto> getTransfersByStatus(int account_id){
        List<ClientTransferDto> transfers = new ArrayList<>();

        String sql = "SELECT " +
                "transfer.transfer_id, " +
                "transfer.account_to, " +
                "transfer.account_from, " +
                "transfer.amount, " +
                "transfer_status.transfer_status_desc, " +
                "transfer_type.transfer_type_desc, " +
                "user_to.username AS to, " +
                "user_from.username AS from " +
                "FROM transfer " +
                "JOIN  account AS account_from ON transfer.account_from = account_from.account_id " +
                "JOIN account AS account_to ON transfer.account_to = account_to.account_id " +
                "JOIN tenmo_user AS user_from ON account_from.user_id = user_from.user_id " +
                "JOIN tenmo_user AS user_to ON account_to.user_id = user_to.user_id " +
                "JOIN transfer_status ON transfer_status.transfer_status_id = transfer.transfer_status_id " +
                "JOIN transfer_type ON transfer_type.transfer_type_id = transfer.transfer_type_id " +
                "WHERE (transfer.account_from = ? OR transfer.account_to = ?) AND transfer_status.transfer_status_desc = 'Pending'" +
                "ORDER BY transfer.transfer_id DESC";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id, account_id);

        while(results.next()){
            transfers.add(mapRowToClientTransferObject(results));
        }

        return transfers;
    }

    public ClientTransferDto getClientTransferById(int account_id){
        String sql = "SELECT " +
                "transfer.transfer_id, " +
                "transfer.account_to, " +
                "transfer.account_from, " +
                "transfer.amount, " +
                "transfer_status.transfer_status_desc, " +
                "transfer_type.transfer_type_desc, " +
                "user_to.username AS to, " +
                "user_from.username AS from " +
                "FROM transfer " +
                "JOIN  account AS account_from ON transfer.account_from = account_from.account_id " +
                "JOIN account AS account_to ON transfer.account_to = account_to.account_id " +
                "JOIN tenmo_user AS user_from ON account_from.user_id = user_from.user_id " +
                "JOIN tenmo_user AS user_to ON account_to.user_id = user_to.user_id " +
                "JOIN transfer_status ON transfer_status.transfer_status_id = transfer.transfer_status_id " +
                "JOIN transfer_type ON transfer_type.transfer_type_id = transfer.transfer_type_id " +
                "WHERE transfer.transfer_id = ? ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);

        if(results.next()){
            return mapRowToClientTransferObject(results);
        }
        return null;
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

    private ClientTransferDto mapRowToClientTransferObject(SqlRowSet rs) {
        ClientTransferDto clientTransferDto = new ClientTransferDto();
        AccountDto sender = new AccountDto();
        AccountDto receiver = new AccountDto();

        clientTransferDto.setTransfer_id(rs.getInt("transfer_id"));
        clientTransferDto.setAmount(rs.getDouble("amount"));
        clientTransferDto.setStatus(rs.getString("transfer_status_desc"));
        clientTransferDto.setType(rs.getString("transfer_type_desc"));
        sender.setAccount_id(rs.getInt("account_from"));
        sender.setUsername(rs.getString("from"));
        clientTransferDto.setSender(sender);
        receiver.setAccount_id(rs.getInt("account_to"));
        receiver.setUsername(rs.getString("to"));
        clientTransferDto.setReceiver(receiver);
        return clientTransferDto;
    }
}
