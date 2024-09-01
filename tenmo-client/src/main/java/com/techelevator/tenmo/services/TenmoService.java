package com.techelevator.tenmo.services;

import com.techelevator.exceptions.*;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private HttpEntity<TransferStatusDto> makeTransferStatusDtoEntity(TransferStatusDto transferStatusDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(transferStatusDto, headers);
    }

    public Account getUserAccount(String username) {
        try {
            return restTemplate.exchange(API_BASE_URL + "user/account?username=" + username, HttpMethod.GET, getAuthHeaders(), Account.class).getBody();
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public double getAccountBalance() {
        try {
            return restTemplate.exchange(API_BASE_URL + "user/account/balance", HttpMethod.GET, getAuthHeaders(), double.class).getBody();
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public Account[] getAccounts() {
        try {
            return restTemplate.exchange(API_BASE_URL + "user/accounts", HttpMethod.GET, getAuthHeaders(), Account[].class).getBody();
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public Transfer[] getTransferByUser() {
        try {
            return restTemplate.exchange(API_BASE_URL + "user/account/transfers", HttpMethod.GET, getAuthHeaders(), Transfer[].class).getBody();
        }
        catch (RestClientResponseException  e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public Transfer getTransferById(int id) {
        try {
            return restTemplate.exchange(API_BASE_URL + "user/account/transfer/" + id, HttpMethod.GET, getAuthHeaders(), Transfer.class).getBody();
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public Transfer[] viewPending() {
        try {
            return restTemplate.exchange(API_BASE_URL + "user/account/transfers/pending", HttpMethod.GET, getAuthHeaders(), Transfer[].class).getBody();
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public void sendTEBucksTo(TransferDto transfer) throws TenmoRequestException {
        try {
            restTemplate.exchange(API_BASE_URL + "user/account/transfers/send", HttpMethod.POST, makeTransferDtoEntity(transfer), Void.class);
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public void requestTEBucksFrom(TransferDto transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "user/account/transfers/request", HttpMethod.POST, makeTransferDtoEntity(transfer), Void.class);
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    public void changeTransferStatus(TransferStatusDto transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "user/account/transfer", HttpMethod.PUT, makeTransferStatusDtoEntity(transfer), Void.class);
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            throw new TenmoRequestException(extractMessageFromServerResponse(e.getMessage()), e.getRawStatusCode());
        }
    }

    private String extractMessageFromServerResponse(String response){
        String regex = "\"message\":\\s*\"(.*?)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);

        // Extracting the error message
        if (matcher.find()) {
            String errorMessage = matcher.group(1);
            return errorMessage;
        } else {
            return "No error message found.";
        }
    }
}
