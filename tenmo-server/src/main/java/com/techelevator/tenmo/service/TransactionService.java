package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.dto.request.TransferDto;
import com.techelevator.tenmo.model.dto.response.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    @Autowired
    TransferDao transferDao;
    @Autowired
    AccountDao accountDao;

    @Transactional
    public void sendFunds(int senderId, TransferDto transferData){
        // take funds from sender, give to receiver
        transferDao.sendTeBucks(senderId, transferData);
        this.transferFunds(senderId, transferData);
    }

    public void requestFunds(int receiverId, TransferDto transferData){
        transferDao.requestTeBucks(receiverId, transferData);
    }

    @Transactional
    public void approveRequest(int transactionId, int senderId){
        transferDao.approveTransaction(transactionId);
        Transfer transferData = transferDao.getTransferById(transactionId);
        this.transferFunds(senderId, new TransferDto(transferData.getAccount_to(), transferData.getAmount(), transferData.getMessage()));
    }

    @Transactional
    private void transferFunds(int senderId, TransferDto transferData){
        accountDao.removeFromBalance(senderId, transferData.getAmount());
        accountDao.addToBalance(transferData.getAccount(), transferData.getAmount());
        accountDao.createFriendship(senderId, transferData.getAccount());
    }
}
