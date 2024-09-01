package com.techelevator.tenmo;

import com.techelevator.exceptions.*;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.math.BigDecimal;

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

        String TEnmo = consoleService.readAsciiArtFromFile("src/main/resources/banners/banner.txt");
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
                try {
                    handleRegister();
                } catch (LoginFailureException e) {
                    System.out.println("\n" + e.getMessage());
                }
            } else if (menuSelection == 2) {
                try {
                    handleLogin();
                } catch (LoginFailureException e) {
                    System.out.println("\n" + e.getMessage());
                }

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
            throw new LoginFailureException("User already exists", 418);
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);

        if (currentUser == null) {
            throw new LoginFailureException("Username or Password incorrect", 404);
        }
        tenmoService.setToken(currentUser.getToken());
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
        String balanceBanner = consoleService.readAsciiArtFromFile("src/main/resources/banners/Balance.txt");
        System.out.println(balanceBanner + "\n");
        double balance = 0.0;
        try {
            balance = tenmoService.getAccountBalance();
        }
        catch(TenmoRequestException e){
            System.out.println(e.getMessage());
            return;
        }
        if (balance <= 50) {
            String sadFace = consoleService.readAsciiArtFromFile("src/main/resources/banners/sad.txt");
            System.out.print(sadFace + "    $");
            System.out.print(balance + "\n");
            System.out.println("____________________________");
        } else {
            String happyFace = consoleService.readAsciiArtFromFile("src/main/resources/banners/happy.txt");
            System.out.print(happyFace + "    $");
            System.out.print(balance + "\n");
            System.out.println("____________________________");
        }


    }

    private void viewTransferHistory() {
        Transfer[] pastTransfers = new Transfer[0];
        try {
            pastTransfers = tenmoService.getTransferByUser();
        }
        catch(TenmoRequestException e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("__________________________________");
        for (Transfer transfer : pastTransfers) {
            System.out.println(transfer);
        }
        System.out.println("__________________________________");

    }

    private void viewPendingRequests() {
        boolean stay = true;
        Transfer[] pending = new Transfer[0];
        while (stay) {
            try {
                pending = tenmoService.viewPending();
            }
            catch(TenmoRequestException e){
                System.out.println(e.getMessage());
                return;
            }
            if (pending.length == 0) {
                System.out.println("You are all caught up!");
                return;
            }
            System.out.println(pending.length);
            for (Transfer transfer : pending) {
                System.out.println(transfer);
            }
            stay = false;
        }

        Transfer transfer = null;
        boolean isValid = false;
        while (!isValid) {
            String option = consoleService.promptForString("Enter transaction id to change or hit x to exit: ");
            if (option.equals("x")) {
                return;
            }
            int transactionIdToChange = 0;
            try {
                transactionIdToChange = Integer.parseInt(option);
                transfer = tenmoService.getTransferById(transactionIdToChange);
            }
            catch (NumberFormatException e){
                System.out.println("Please enter a valid number");
                continue;
            }
            catch(TenmoRequestException e){
                System.out.println(e.getMessage());
                return;
            }
            if (transfer != null) {
                isValid = true;
            }
            System.out.println("Please enter a valid transaction id");
        }
        isValid = false;
        while (!isValid) {
            int option = consoleService.promptForInt("Press 1 to approve \nPress 2 to reject: ");
            TransferStatusDto updated = new TransferStatusDto();
            updated.setId(transfer.getTransfer_id());
            updated.setSendingAccount(transfer.getSender().getAccount_id());
            if (option == 1) {
                updated.setStatus("Approved");
            } else if (option == 2) {
                updated.setStatus("Rejected");
            } else {
                System.out.println("Please enter a valid option");
                continue;
            }
            try {
                tenmoService.changeTransferStatus(updated);
            }
            catch(TenmoRequestException e){
                System.out.println(e.getMessage());
                return;
            }
            isValid = true;
        }
    }

    private void sendBucks() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;
        while (!isValid) {
            System.out.println("Select user to send TE bucks");
            System.out.println("__________________________________\n");
            Account[] accounts = tenmoService.getAccounts();
            for (int i=0; i<accounts.length; i++) {
                System.out.println(i+1 + ") " + accounts[i]);
                System.out.println("__________________________________");
            }
            int input = consoleService.promptForInt("Select user by index: ");
            if(input > accounts.length || input < 1){
                System.out.println("please enter a valid index");
                continue;
            }
            accountToReceiveMoney = tenmoService.getUserAccount(accounts[input-1].getUsername());
            if (accountToReceiveMoney != null) {
                isValid = true;
            }
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to send: ");
        TransferDto transfer = new TransferDto();
        transfer.setAccount(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.floatValue());
        String message = consoleService.promptForString("What's this for?: ");
        transfer.setMessage(message);
        try {
            tenmoService.sendTEBucksTo(transfer);
        }
        catch(TenmoRequestException e){
            System.out.println(e.getMessage());
        }
    }

    private void requestBucks() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;
        while (!isValid) {
            System.out.println("Select user to request TE bucks from");
            System.out.println("__________________________________\n");
            Account[] accounts = tenmoService.getAccounts();
            for (int i=0; i<accounts.length; i++) {
                System.out.println(i+1 + ") " + accounts[i]);
                System.out.println("__________________________________");
            }
            int input = consoleService.promptForInt("Select user by index: ");
            if(input > accounts.length || input < 1){
                System.out.println("please enter a valid index");
                continue;
            }
            try {
                accountToReceiveMoney = tenmoService.getUserAccount(accounts[input - 1].getUsername());
            }
            catch(TenmoRequestException e){
                System.out.println(e.getMessage());
                return;
            }
            if (accountToReceiveMoney != null) {
                isValid = true;
            }
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to receive: ");
        TransferDto transfer = new TransferDto();
        transfer.setAccount(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.doubleValue());
        String message = consoleService.promptForString("What's this for?: ");
        transfer.setMessage(message);
        tenmoService.requestTEBucksFrom(transfer);
    }
}
