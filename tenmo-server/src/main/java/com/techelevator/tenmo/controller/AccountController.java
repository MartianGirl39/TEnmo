package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.dto.ClientTransferDto;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import com.techelevator.tenmo.model.dto.UserAccountDto;
import com.techelevator.tenmo.model.dto.*;
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
    private TransferTypeDao transferTypeDao;
    @Autowired
    private TransferStatusDao transferStatusDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path = "/user/account/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            return account.getBalance();
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
        catch (ResponseStatusException e){
            throw e;
        }
    }

    // sends a transfers
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "user/account/transfers/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody TransferDto transfer, Principal principal) {
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            if (account.getBalance() < transfer.getAmount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send an amount that is greater than your balance");
            } else if (transfer.getAmount() < 0.01) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send an amount that is 0 or less dollars");
            }
            // create a new transfer
            transferDao.sendTeBucks(account.getAccount_id(), transfer.getAccount(), transfer.getAmount(), transfer.getMessage());
            // adjust balances accordingly
            accountDao.transferBalance(account.getAccount_id(), transfer.getAccount(), transfer.getAmount());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "user/account/transfers/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody TransferDto transfer, Principal principal) {
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            if (account.getAccount_id() == transfer.getAccount()) {
                // throw 403
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send money to yourself");
            }
            transferDao.requestTeBucks(transfer.getAccount(), account.getAccount_id(), transfer.getAmount(), transfer.getMessage());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "user/account/transfer", method = RequestMethod.PUT)
    public void updateTransfer(@RequestBody TransferStatusDto transferData, Principal principal) {
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            Transfer transfer = transferDao.getTransferById(transferData.getId());
            if(transfer.getTransfer_status_id() != 1){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot change transfer status unless status is pending");
            }
            switch (transferData.getStatus()){
                case "Approved":
                    if(account.getAccount_id() != transfer.getAccount_from()){
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot Approve their own requests");
                    }
                    transferDao.approveTransaction(transferData.getId());
                    break;
                case "Rejected":
                    if(account.getAccount_id() != transfer.getAccount_from()){
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot Approve their own requests");
                    }
                    transferDao.rejectTransaction(transferData.getId());
                    break;
                case "Canceled":
                    if(account.getAccount_id() != transfer.getAccount_to()){
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sender cannot cancel requests, did you mean Reject?");
                    }
                    transferDao.cancelTransaction(transferData.getId());
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid transfer status");
            }
            accountDao.transferBalance(transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/user/account", method = RequestMethod.GET)
    public AccountDto getUserAccount(@RequestParam(required = false) String username, Principal principal) {
        AccountDto account = null;
        try {
            if (username == null) {
                return this.getAndValidateUser(principal.getName());
            } else {
                account = accountDao.getAccountDtoByUsername(username);
                if (account == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                return account;
            }
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/user/account/{id}", method = RequestMethod.GET)
    public AccountDto getUserById(@PathVariable int id) {
        try {
            AccountDto account = accountDao.getAccountDtoById(id);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
            }
            return account;
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/user/accounts", method = RequestMethod.GET)
    public List<AccountDto> getUsers(Principal principal) {
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            List<AccountDto> accounts = accountDao.getAccounts(account.getAccount_id());
            return accounts;
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/user/account/transfers", method = RequestMethod.GET)
    public List<ClientTransferDto> getAllTransfersForUser(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            return transferDao.getTransfers(account.getAccount_id());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/user/account/transfer/{id}", method = RequestMethod.GET)
    public ClientTransferDto getTransferById2(@PathVariable int id) {
        try {
            ClientTransferDto transfer = transferDao.getClientTransferById(id);
            if (transfer == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find transaction");
            }
            return transfer;
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/user/account/transfers/pending", method = RequestMethod.GET)
    public List<ClientTransferDto> getAllTransfersForUserByStatus(Principal principal) {
        try {
            UserAccountDto account = this.getAndValidateUser(principal.getName());
            return transferDao.getTransfersByStatus(account.getAccount_id());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    private UserAccountDto getAndValidateUser(String username) {
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user, are you logged in as a valid TEnmo user?");
        }
        UserAccountDto account = accountDao.getAccountByUserId(user.getId());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find account, are you logged in as a valid TEnmo user?");
        }
        return account;
    }
}
