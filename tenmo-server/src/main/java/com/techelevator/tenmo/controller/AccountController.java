package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.TransferStatusDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private TransferStatusDao transferStatusDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path = "account", method = RequestMethod.GET)
    public Account getAccount(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        return accountDao.getAccountByUserId(user.getId());
    }

    @RequestMapping(path = "account/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        return accountDao.getAccountByUserId(user.getId()).getBalance();
    }

    @RequestMapping(path = "account/transfers", method = RequestMethod.GET)
    public List<Transfer> getTransferByUser(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(user.getId());
        return transferDao.getTransferByUser(account.getAccount_id());
    }

    @RequestMapping(path = "transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id) {
        return transferDao.getTransferById(id);
    }

    @RequestMapping(path = "account/transfers/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody Transfer transfer, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(user.getId());
        if (account.getAccount_id() != transfer.getAccount_from()) {
            // throw 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        // create a new transfer
        transferDao.sendTeBucks(transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
        // adjust balances accordingly
        accountDao.transferBalance(account.getAccount_id(), transfer.getAccount_to(), transfer.getAmount());
    }

    @RequestMapping(path = "account/transfers/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody Transfer transfer, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(user.getId());
        if (account.getAccount_id() != transfer.getAccount_to()) {
            // throw 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        transferDao.requestTeBucks(account.getAccount_id(), transfer.getAccount_to(), transfer.getAmount());
    }

    @RequestMapping(path = "/account/transfer", method = RequestMethod.PUT)
    public void updateTransfer(@RequestBody Transfer transfer, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        Account account = accountDao.getAccountByUserId(user.getId());
        if (account.getAccount_id() != transfer.getAccount_from()) {
            // throw 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        transferDao.updateTransactionStatus(transfer);
    }

    @RequestMapping(path = "/account/transfer/{type}", method = RequestMethod.GET)
    public List<Transfer> viewPending(@PathVariable String type, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        TransferStatus status = transferStatusDao.getStatusByName(type);
        Account account = accountDao.getAccountByUserId(user.getId());
        return transferDao.getTransfersByType(account.getAccount_id(), status.getTransfer_status_desc());
    }
}
