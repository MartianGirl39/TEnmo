package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.dto.response.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Service
public class AccountValidationService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    public UserAccountDto getAndValidateUser(String username) {
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

    public User getValidatedUser(Principal principal){
        return userDao.getUserByUsername(principal.getName());
    }
}
