package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {
    private final TenmoService tenmoService = new TenmoService();

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {

        String TEnmo = consoleService.readAsciiArtFromFile("C:/Users/Student/workspace/java-blue-module2capstone-team6/tenmo-client/src/main/resources/banner.txt");
        System.out.println(TEnmo);

        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        tenmoService.setToken(currentUser.getToken() );
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        System.out.println(tenmoService.getAccountBalance() );
    }

    private void viewTransferHistory() {
        System.out.println(tenmoService.getTransferByUser() );

    }

    private void viewPendingRequests() {
        Transfer[] pending = tenmoService.viewPending("Pending");
        System.out.println(pending.length);
        for(Transfer transfer: pending){
            System.out.println(transfer);
        }

        Transfer transfer = null;
        boolean isValid = false;
        while(!isValid){
            String option = consoleService.promptForString("Enter transaction id to change or hit x to exit");
            if(option == "x"){
                isValid = true;
                continue;
            }
            int transactionIdToChange = 0;
            try {
                transactionIdToChange = Integer.parseInt(option);
            }
            catch (NumberFormatException e){
                System.out.println("Enter a valid number or x");
                continue;
            }
            transfer = tenmoService.getTransferById(transactionIdToChange);
            if(transfer != null){
                isValid = true;
            }
            System.out.println("Please enter a valid transaction id");
        }
        isValid = false;
        while(!isValid){
            int option = consoleService.promptForInt("Press 1 to approve \nPress 2 to reject");
            if (option == 1){
                Transfer updated = transfer;
                updated.setTransfer_status_id(2);
                tenmoService.changeTransferStatus(updated);
                break;
            }
            else if (option == 2){
                Transfer updated = transfer;
                updated.setTransfer_status_id(3);
                tenmoService.changeTransferStatus(updated);
                break;
            }
            else {
                System.out.println("Please enter a valid option");
            }
        }
    }

    private void sendBucks() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;
        while(!isValid) {
            System.out.println("Select user to send TE bucks");
            System.out.println("__________________________________\n");
            Account[] accounts = tenmoService.getAccounts();
            for (Account account : accounts) {
                System.out.println(account);
                System.out.println("__________________________________");
            }
            int input = consoleService.promptForInt("Select user by account_id: ");
            accountToReceiveMoney = tenmoService.getAccountById(input);
            if(accountToReceiveMoney != null){
                isValid = true;
            }
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to give: ");
        Transfer transfer = new Transfer();
        transfer.setAccount_to(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.doubleValue());
        transfer.setAccount_from(tenmoService.getUserAccount().getAccount_id());
        tenmoService.sendTEBucksTo(transfer);
    }

    private void requestBucks() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;
        while(!isValid) {
            System.out.println("Select user to request TE bucks from");
            System.out.println("__________________________________\n");
            Account[] accounts = tenmoService.getAccounts();
            for (Account account : accounts) {
                System.out.println(account);
                System.out.println("__________________________________");
            }
            int input = consoleService.promptForInt("Select user by account_id: ");
            accountToReceiveMoney = tenmoService.getAccountById(input);if(accountToReceiveMoney != null){
                isValid = true;
            }
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to receive: ");
        Transfer transfer = new Transfer();
        transfer.setAccount_from(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.doubleValue());
        transfer.setAccount_to(tenmoService.getUserAccount().getAccount_id());
        tenmoService.requestTEBucksFrom(transfer);
    }

}
