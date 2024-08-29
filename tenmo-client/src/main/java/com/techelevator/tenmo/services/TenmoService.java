package com.techelevator.tenmo.services;

import com.techelevator.exceptions.InsufficientFunds;
import com.techelevator.exceptions.TenmoRequestException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.TransferStatusDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.channels.AcceptPendingException;
import java.util.List;

public class TenmoService {
    private final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public HttpEntity<Void> getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);

    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<TransferDto> makeTransferDtoEntity(TransferDto transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<TransferStatusDto> makeTransferStatusDtoEntity(TransferStatusDto transferStatusDto){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(transferStatusDto, headers);
    }

    public Account getUserAccount() {
        return restTemplate.exchange(API_BASE_URL + "account", HttpMethod.GET, getAuthHeaders(), Account.class).getBody();
    }

    public double getAccountBalance() {
        HttpEntity entity = getAuthHeaders();
        double balance = restTemplate.exchange(API_BASE_URL + "account/balance", HttpMethod.GET, entity, double.class).getBody();
        return balance;
    }

    public Account[] getAccounts(){
        return restTemplate.exchange(API_BASE_URL + "accounts", HttpMethod.GET, getAuthHeaders(), Account[].class).getBody();
    }

    public Account getAccountById(int id){
        return restTemplate.exchange(API_BASE_URL + "account/" + id, HttpMethod.GET, getAuthHeaders(), Account.class).getBody();
    }

    public Transfer[] getTransferByUser() {
        HttpEntity entity = getAuthHeaders();
        return restTemplate.exchange(API_BASE_URL + "account/transfers", HttpMethod.GET, entity, Transfer[].class).getBody();
    }

    public Transfer getTransferById(int id) {
        HttpEntity entity = getAuthHeaders();
        Transfer transfer = restTemplate.exchange(API_BASE_URL + "transfer/" + id, HttpMethod.GET, entity, Transfer.class).getBody();
        return transfer;
    }

    public Transfer[] viewPending(String type) {
        HttpEntity entity = getAuthHeaders();
        return restTemplate.exchange(API_BASE_URL + "/account/transfer/" + type, HttpMethod.GET, entity, Transfer[].class).getBody();
    }

    // TODO: make this with a transfer dto
    public void sendTEBucksTo(TransferDto transfer)throws TenmoRequestException {
        try {
            restTemplate.exchange(API_BASE_URL + "account/transfers/send", HttpMethod.POST, makeTransferDtoEntity(transfer), Void.class);
        }
        catch (HttpClientErrorException e){
            throw new InsufficientFunds("Insufficient Funds", e.getStatusCode().value());
        }
    }

    public void requestTEBucksFrom(TransferDto transfer){
        restTemplate.exchange(API_BASE_URL + "account/transfers/request", HttpMethod.POST, makeTransferDtoEntity(transfer), Void.class);
    }

    public void changeTransferStatus(TransferStatusDto transfer){
        restTemplate.exchange(API_BASE_URL + "account/transfer", HttpMethod.PUT, makeTransferStatusDtoEntity(transfer), Void.class);
    }
}
