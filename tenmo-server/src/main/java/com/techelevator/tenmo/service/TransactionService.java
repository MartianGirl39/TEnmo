package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.ExpenseDao;
import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.dto.request.GroupExpenseDto;
import com.techelevator.tenmo.model.dto.request.TransferDto;
import com.techelevator.tenmo.model.dto.response.ClientGroupExpenseDto;
import com.techelevator.tenmo.model.dto.response.ClientTransferDto;
import com.techelevator.tenmo.model.dto.response.ContributionDto;
import com.techelevator.tenmo.model.dto.response.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    TransferDao transferDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    ExpenseDao expenseDao;

    @Transactional
    public ClientTransferDto sendFunds(int senderId, TransferDto transferData){
        // take funds from sender, give to receiver
        ClientTransferDto newTransfer = transferDao.sendTeBucks(senderId, transferData);
        this.transferFunds(senderId, transferData);
        return newTransfer;
    }

    public void requestFunds(int receiverId, TransferDto transferData){
        transferDao.requestTeBucks(receiverId, transferData);
    }

    @Transactional
    public void approveRequest(int transactionId, int senderId){
        transferDao.updateTransactionStatus(1, transactionId);
        Transfer transferData = transferDao.getTransferById(transactionId);
        this.transferFunds(senderId, new TransferDto(transferData.getAccount_to(), transferData.getAmount(), transferData.getMessage()));
    }

    @Transactional
    private void transferFunds(int senderId, TransferDto transferData){
        accountDao.removeFromBalance(senderId, transferData.getAmount());
        accountDao.addToBalance(transferData.getAccount(), transferData.getAmount());
        accountDao.createFriendship(senderId, transferData.getAccount());
    }

    @Transactional
    public void settleExpense(int expenseId, int settlerId){
        GroupExpenseDto expense = expenseDao.getGroupExpenseAsGroupExpenseDto(expenseId);
        List<ContributionDto> contributions = expenseDao.getExpenseContributions(expenseId);
        UserAccountDto account = new UserAccountDto();

        TransferDto settlingTransfer = new TransferDto();
        settlingTransfer.setAccount(settlerId);
        settlingTransfer.setMessage("settling expense");

        ClientTransferDto toLink = new ClientTransferDto();

        double total = 0.0;

        // I want to create a transfer for each person of the amount they are given
        for(ContributionDto contribution : contributions){
            account = accountDao.getAccountByUserId(contribution.getAccount().getAccount().getAccount_id() - 1000);
            if(contribution.getAccount().getAccount().getAccount_id() != settlerId) {
                if(account.getBalance() < contribution.getAmount()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot settle transaction because some users don't have enough money in their accounts");
                }
                settlingTransfer.setAmount(contribution.getAmount());
                toLink = this.sendFunds(contribution.getAccount().getAccount().getAccount_id(), settlingTransfer);
                expenseDao.linkTransfer(toLink.getTransfer_id(), expenseId);
            }
            total += contribution.getAmount();
        }

        // I want the receiver to receive a transfer for the total from the person who settled the expense
        if(total < expense.getTotal()){
            expense.setTotal(expense.getTotal() - total);
            expenseDao.updateGroupExpense(expenseId, expense);
        }
        else if (total > expense.getTotal()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "contributors have given too much money");
        }

        TransferDto finalTransfer = new TransferDto();

        finalTransfer.setAmount(total);
        finalTransfer.setMessage("settled expense");
        finalTransfer.setAccount(expense.getAccount_owed());

        account = accountDao.getUserAccountById(settlerId);
        if(account.getBalance() < total){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot settle transaction because you don't have enough funds in your account");
        }

        toLink = this.sendFunds(settlerId, finalTransfer);
        expenseDao.linkTransfer(toLink.getTransfer_id(), expenseId);
        expenseDao.updateGroupExpenseStatus(expenseId, 2);
    }
}
