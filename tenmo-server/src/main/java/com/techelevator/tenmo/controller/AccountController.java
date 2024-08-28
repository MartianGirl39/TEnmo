package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path="accounts/{id}", method= RequestMethod.GET)
    public Account getAccountById(@PathVariable int id){
        return null;
    }

    @RequestMapping(path="accounts/{id}/balance", method = RequestMethod.GET)
    public double getAccountBalance(@PathVariable int id){
        Account account = this.accountDao.getAccountById(id);
        return account.getBalance();
    }

    @RequestMapping(path="account/transfer", method=RequestMethod.GET)
    public List<Transfer> getTransferByUser(Principal principal){
        return new ArrayList<>();
    }

    @RequestMapping(path="transfer/{id}", method=RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id){
        return null;
    }

    @RequestMapping(path="account/transfer", method=RequestMethod.POST)
    public void createNewTransfer(@RequestBody @Valid Transfer transfer){

    }
}
