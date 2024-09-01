package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.User;
<<<<<<< HEAD
import com.techelevator.tenmo.model.dto.ClientTransferDto;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import com.techelevator.tenmo.model.dto.UserAccountDto;
=======
import com.techelevator.tenmo.model.dto.*;
>>>>>>> 48f84f9a3330be474b67539f14c83ee44aa62401
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.plugins.tiff.GeoTIFFTagSet;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private TransferTypeDao transferTypeDao;
    @Autowired
    private TransferStatusDao transferStatusDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path = "/account/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        // fetching user from the principal
        try {
            User user = userDao.getUserByUsername(principal.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return account.getBalance();
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // sends a transfers
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/account/transfers/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody TransferDto transfer, Principal principal) {
        try {
            User user = userDao.getUserByUsername(principal.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            if (account.getAccount_id() == transfer.getAccount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot send money to yourself");
            }
            if (account.getBalance() < transfer.getAmount() || transfer.getAmount() < 0.01) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            // create a new transfer
            transferDao.sendTeBucks(account.getAccount_id(), transfer.getAccount(), transfer.getAmount(), transfer.getMessage());
            // adjust balances accordingly
            accountDao.transferBalance(account.getAccount_id(), transfer.getAccount(), transfer.getAmount());
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/account/transfers/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody TransferDto transfer, Principal principal) {
        try {
            User user = userDao.getUserByUsername(principal.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            if (account.getAccount_id() == transfer.getAccount()) {
                // throw 403
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            transferDao.requestTeBucks(transfer.getAccount(), account.getAccount_id(), transfer.getAmount(), transfer.getMessage());
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/account/transfer", method = RequestMethod.PUT)
    public void updateTransfer(@RequestBody TransferStatusDto transfer, Principal principal) {
        try {
            User user = userDao.getUserByUsername(principal.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            if (account.getAccount_id() != transfer.getSendingAccount()) {
                // throw 403
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            TransferStatus status = transferStatusDao.getStatusByName(transfer.getStatus());
            if (status == null) {
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
            }
            Transfer fullTransfer = transferDao.getTransferById(transfer.getId());
            if (fullTransfer.getTransfer_status_id() != 1) {
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
            }
            transferDao.updateTransactionStatus(transfer.getId(), status.getTransfer_status_id());
            if (status.getTransfer_status_id() == 2) {
                accountDao.transferBalance(fullTransfer.getAccount_from(), fullTransfer.getAccount_to(), fullTransfer.getAmount());
            }
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path="/user/account", method=RequestMethod.GET)
    public AccountDto getUserAccount(@RequestParam(required = false) String username, Principal principal){
        User user = null;
        try {
            if (username == null) {
                user = userDao.getUserByUsername(principal.getName());
                if (user == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                UserAccountDto account = accountDao.getUserAccountByUserId(user.getId());
                if(account == null){
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                return account;
            } else {
                AccountDto account = accountDao.getAccountDtoByUsername(username);
                if(account == null){
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                return account;
            }
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path="/user/account/{id}", method=RequestMethod.GET)
    public AccountDto getUserById(@PathVariable int id){
        try {
            AccountDto account = accountDao.getAccountDtoById(id);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return account;
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path="/user/accounts", method=RequestMethod.GET)
    public List<AccountDto> getUsers(Principal principal){
        List<AccountDto> userDtos = new ArrayList<>();
        User user = userDao.getUserByUsername(principal.getName());
        try {
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            List<Account> accounts = accountDao.listUser(account.getAccount_id());
            for (Account acc : accounts) {
                AccountDto newUser = new AccountDto();
                newUser.setAccount_id(acc.getAccount_id());
                newUser.setUsername(userDao.getUserById(acc.getUser_id()).getUsername());
                userDtos.add(newUser);
            }
            return userDtos;
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path="/user/account/transfers", method=RequestMethod.GET)
    public List<ClientTransferDto> getAllTransfersForUser(Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        try {
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return transferDao.getTransfers(account.getAccount_id());
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path="/user/account/transfer/{id}", method=RequestMethod.GET)
    public ClientTransferDto getTransferById2(@PathVariable int id){
        try {
            ClientTransferDto transfer = transferDao.getClientTransferById(id);
            if(transfer == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return transfer;
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path="/user/account/transfers/pending", method=RequestMethod.GET)
    public List<ClientTransferDto> getAllTransfersForUserByStatus(Principal principal){
        try {
            User user = userDao.getUserByUsername(principal.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Account account = accountDao.getAccountByUserId(user.getId());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return transferDao.getTransfersByStatus(account.getAccount_id());
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
