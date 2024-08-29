package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

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

    public double getAccountBalance() {
        HttpEntity entity = getAuthHeaders();
        double balance = restTemplate.exchange(API_BASE_URL + "account/balance", HttpMethod.GET, entity, double.class).getBody();
        return balance;
    }

    public List<Transfer> getTransferByUser() {
        HttpEntity entity = getAuthHeaders();
        List<Transfer> transfers = restTemplate.exchange(API_BASE_URL + "account/transfers", HttpMethod.GET, entity, List.class).getBody();
        return transfers;
    }

    public Transfer getTransferById(int id) {
        HttpEntity entity = getAuthHeaders();
        Transfer transfer = restTemplate.exchange(API_BASE_URL + "transfer/" + id, HttpMethod.GET, entity, Transfer.class).getBody();
        return transfer;
    }

    public List<Transfer> viewPending(String type) {
        HttpEntity entity = getAuthHeaders();
        List<Transfer> transfers = restTemplate.exchange(API_BASE_URL + "/account/transfer/" + type, HttpMethod.GET, entity, List.class).getBody();
        return transfers;
    }
}
