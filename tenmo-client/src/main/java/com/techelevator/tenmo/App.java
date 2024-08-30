package com.techelevator.tenmo;

import com.techelevator.exceptions.InsufficientFunds;
import com.techelevator.exceptions.LoginFailureException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;
import org.springframework.web.client.RestClientException;

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

        double balance = tenmoService.getAccountBalance();
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
        Transfer[] pastTransfers = tenmoService.getTransferByUser();
        System.out.println("__________________________________");
        for (Transfer transfer : pastTransfers) {
            System.out.println(transfer);
        }
        System.out.println("__________________________________");

    }

    private void viewPendingRequests() {
        boolean stay = true;
        while (stay) {
            Transfer[] pending = tenmoService.viewPending();
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
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number or x");
                continue;
            }
            transfer = tenmoService.getTransferById(transactionIdToChange);
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
                try {
                    tenmoService.changeTransferStatus(updated);
                } catch (RestClientException e) {
                    System.out.println("User cannot approve their own requests");
                }
                break;
            } else if (option == 2) {
                updated.setStatus("Rejected");
                try {
                    tenmoService.changeTransferStatus(updated);
                } catch (RestClientException e) {
                    System.out.println("User cannot Reject their own requests");
                }
                break;
            } else {
                System.out.println("Please enter a valid option");
            }
        }
    }

    private void sendBucks() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;
        while (!isValid) {
            System.out.println("Select user to send TE bucks");
            System.out.println("__________________________________\n");
            Account[] accounts = tenmoService.getAccounts();
            for (Account account : accounts) {
                System.out.println(account);
                System.out.println("__________________________________");
            }
            String input = consoleService.promptForString("Select user: ");
            accountToReceiveMoney = tenmoService.getUserAccount(input);
            if (accountToReceiveMoney != null) {
                isValid = true;
            }
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to send: ");
        TransferDto transfer = new TransferDto();
        transfer.setAccount(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.floatValue());
        try {
            tenmoService.sendTEBucksTo(transfer);
        } catch (InsufficientFunds e) {
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
            for (Account account : accounts) {
                System.out.println(account);
                System.out.println("__________________________________");
            }
            String input = consoleService.promptForString("Select user by account_id: ");
            accountToReceiveMoney = tenmoService.getUserAccount(input);
            if (accountToReceiveMoney != null) {
                isValid = true;
            }
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to receive: ");
        TransferDto transfer = new TransferDto();
        transfer.setAccount(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.doubleValue());
        tenmoService.requestTEBucksFrom(transfer);
    }

}
