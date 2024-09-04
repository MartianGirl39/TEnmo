package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.dto.request.TransferDto;
import com.techelevator.tenmo.model.dto.request.TransferStatusDto;
import com.techelevator.tenmo.model.dto.response.ClientTransferDto;
import com.techelevator.tenmo.model.dto.response.UserAccountDto;
import com.techelevator.tenmo.service.AccountValidationService;
import com.techelevator.tenmo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path="/user/account")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    TransferDao transferDao;
    @Autowired
    AccountValidationService validator;
    @Autowired
    TransactionService transactionService;

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public ClientTransferDto getTransferById(@PathVariable int id) {
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

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public List<ClientTransferDto> getAllTransfersForUser(Principal principal) {
        User user = validator.getValidatedUser(principal);
        try {
            UserAccountDto account = validator.getAndValidateUser(principal.getName());
            return transferDao.getTransfers(account.getAccount_id());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @RequestMapping(path = "/transfers/pending", method = RequestMethod.GET)
    public List<ClientTransferDto> getAllTransfersForUserByStatus(Principal principal) {
        try {
            UserAccountDto account = validator.getAndValidateUser(principal.getName());
            return transferDao.getTransfersByStatus(account.getAccount_id());
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path="/transfer/send", method=RequestMethod.POST)
    public void sendTransfer(@RequestBody @Valid TransferDto transfer, Principal principal) {
        UserAccountDto account = new UserAccountDto();
        int newTransferId = 0;
        try {
            account = validator.getAndValidateUser(principal.getName());
            if (account.getBalance() < transfer.getAmount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send an amount that is greater than your balance");
            } else if (transfer.getAmount() < 0.01) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send an amount that is 0 or less dollars");
            }
            transactionService.sendFunds(account.getAccount_id(), transfer);
        }
        catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody TransferDto transfer, Principal principal) {
        try {
            UserAccountDto account = validator.getAndValidateUser(principal.getName());
            if (account.getAccount_id() == transfer.getAccount()) {
                // throw 403
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send money to yourself");
            }
            transactionService.requestFunds(account.getAccount_id(), transfer);
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfer", method = RequestMethod.PUT)
    public void updateTransfer(@RequestBody TransferStatusDto transferData, Principal principal) {
        try {
            UserAccountDto account = validator.getAndValidateUser(principal.getName());
            Transfer transfer = transferDao.getTransferById(transferData.getId());
            if(transfer.getTransfer_status_id() != 1){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot change transfer status unless status is pending");
            }
            switch (transferData.getStatus()){
                case "Approved":
                    if(account.getAccount_id() != transfer.getAccount_from()){
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot Approve their own requests");
                    }
                    transactionService.approveRequest(transfer.getTransfer_id(), account.getAccount_id());
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
        } catch (DaoException err) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our servers are having difficulties");
        }
    }
}
