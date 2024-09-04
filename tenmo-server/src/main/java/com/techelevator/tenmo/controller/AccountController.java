package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.dto.response.AccountDto;
import com.techelevator.tenmo.model.dto.response.UserAccountDto;
import com.techelevator.tenmo.service.AccountValidationService;
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
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    AccountValidationService accountValidationService;

    @RequestMapping(path = "/user/account/balance", method = RequestMethod.GET)
    public double getAccountBalance(Principal principal) {
        try {
            UserAccountDto account = accountValidationService.getAndValidateUser(principal.getName());
            return account.getBalance();
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
        catch (ResponseStatusException e){
            throw e;
        }
    }

    @RequestMapping(path = "/user/account", method = RequestMethod.GET)
    public AccountDto getUserAccount(@RequestParam(required = false) String username, Principal principal) {
        AccountDto account = null;
        try {
            if (username == null) {
                return accountValidationService.getAndValidateUser(principal.getName());
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
            UserAccountDto account = accountValidationService.getAndValidateUser(principal.getName());
            List<AccountDto> accounts = accountDao.getAccounts(account.getAccount_id());
            return accounts;
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }
}
