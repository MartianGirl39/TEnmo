package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path="account", method=RequestMethod.GET)
    public Account getAccount(Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        return accountDao.getAccountByUserId(user.getId());
    }

    @RequestMapping(path="account/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        return accountDao.getAccountByUserId(user.getId()).getBalance();
    }

    @RequestMapping(path="account/transfers", method=RequestMethod.GET)
    public List<Transfer> getTransferByUser(Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        return transferDao.getTransferByUser(user.getId());
    }

    @RequestMapping(path="transfer/{id}", method=RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id){
        return transferDao.getTransferById(id);
    }

    @RequestMapping(path="account/transfers/send", method=RequestMethod.POST)
    public void sendTransfer(@RequestBody Transfer transfer, Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        if(user.getId() != transfer.getAccount_from()){
            // throw 403
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        // create a new transfer
        transferDao.sendTeBucks(user.getId(), transfer.getAccount_to(), transfer.getAmount());
        // adjust balances accordingly
        accountDao.transferBalance(user.getId(), transfer.getAccount_to(), transfer.getAmount());
    }

    @RequestMapping(path="account/transfers/request", method=RequestMethod.POST)
    public void requestTransfer(@RequestBody Transfer transfer, Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        if(user.getId() != transfer.getAccount_from()){
            // throw 403
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        transferDao.requestTeBucks(user.getId(), transfer.getAccount_to(), transfer.getAmount());
    }

    @RequestMapping(path="account/transfer/{id}", method=RequestMethod.PUT)
    public void updateTransfer(@RequestBody Transfer transfer){

    }
}
