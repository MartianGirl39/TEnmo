package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.TransferStatusDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
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

    // fetches account of the user who sends in the request via the principal
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account getAccount(Principal principal) {
        // fetch user
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // fetch account
        Account account = accountDao.getAccountByUserId(user.getId());
        // if there is no account on the database that matches the principal
        if(account == null){
            // throw some kind of 400 error
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // return the account that was fetched
        return account;
    }

    @RequestMapping(path="/account/{id}", method=RequestMethod.GET)
    public Account getAccountById(@PathVariable int id){
        // fetch the account
        Account account = accountDao.getAccountById(id);
        // if the account is not found in the database
        if(account == null){
            // throw a 404 error
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return account;
    }

    @RequestMapping(path = "/account/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        // fetching user from the principal
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Account account = accountDao.getAccountByUserId(user.getId());
        if(account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return account.getBalance();
    }

    @RequestMapping(path="/accounts", method=RequestMethod.GET)
    public List<Account> getAccounts(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // fetch account
        Account account = accountDao.getAccountByUserId(user.getId());
        // if there is no account on the database that matches the principal
        if(account == null){
            // throw some kind of 400 error
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return accountDao.listUser(account.getAccount_id());
    }

    @RequestMapping(path = "/account/transfers", method = RequestMethod.GET)
    public List<Transfer> getTransferByUser(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Account account = accountDao.getAccountByUserId(user.getId());
        if(account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transferDao.getTransferByUser(account.getAccount_id());
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id) {
        Transfer transfer = transferDao.getTransferById(id);
        if(transfer == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transfer;
    }

    // sends a transfers
    @RequestMapping(path = "/account/transfers/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody TransferDto transfer, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Account account = accountDao.getAccountByUserId(user.getId());
        if(account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (account.getAccount_id() == transfer.getAccount()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot send money to yourself");
        }
        if (account.getBalance() < transfer.getAmount() || transfer.getAmount() < 0.01){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // create a new transfer
        transferDao.sendTeBucks(account.getAccount_id(), transfer.getAccount(), transfer.getAmount());
        // adjust balances accordingly
        accountDao.transferBalance(account.getAccount_id(), transfer.getAccount(), transfer.getAmount());
    }

    @RequestMapping(path = "/account/transfers/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody TransferDto transfer, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Account account = accountDao.getAccountByUserId(user.getId());
        if(account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (account.getAccount_id() == transfer.getAccount()) {
            // throw 403
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        transferDao.requestTeBucks(transfer.getAccount(), account.getAccount_id(), transfer.getAmount());
    }

    @RequestMapping(path = "/account/transfer", method = RequestMethod.PUT)
    public void updateTransfer(@RequestBody TransferStatusDto transfer, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Account account = accountDao.getAccountByUserId(user.getId());
        if(account == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (account.getAccount_id() != transfer.getSendingAccount()) {
            // throw 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        TransferStatus status = transferStatusDao.getStatusByName(transfer.getStatus());
        if(status == null){
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
        }
        Transfer fullTransfer = transferDao.getTransferById(transfer.getId());
        if(fullTransfer.getTransfer_status_id() != 1){
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
        }
        transferDao.updateTransactionStatus(transfer.getId(), status.getTransfer_status_id());
        if (status.getTransfer_status_id() == 2){
            accountDao.transferBalance(fullTransfer.getAccount_from(), fullTransfer.getAccount_to(), fullTransfer.getAmount());
        }
    }

    @RequestMapping(path = "/account/transfer/{type}", method = RequestMethod.GET)
    public List<Transfer> viewPending(@PathVariable String type, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        TransferStatus status = transferStatusDao.getStatusByName(type);
        Account account = accountDao.getAccountByUserId(user.getId());
        return transferDao.getTransfersByType(account.getAccount_id(), status.getTransfer_status_desc());
    }
}
