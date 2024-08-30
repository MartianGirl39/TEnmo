package com.techelevator.tenmo;

import com.techelevator.exceptions.InsufficientFunds;
import com.techelevator.tenmo.model.*;
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

        String TEnmo = consoleService.readAsciiArtFromFile("C://Users//Student//workspace//java-blue-module2capstone-team6//tenmo-client//src//main//resources//banners//banner.txt/");
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
        tenmoService.setToken(currentUser.getToken());
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
        String balanceBanner = consoleService.readAsciiArtFromFile("C://Users//Student//workspace//java-blue-module2capstone-team6//tenmo-client//src//main//resources//banners//Balance.txt/");
        System.out.println(balanceBanner + "\n");

        double balance = tenmoService.getAccountBalance();
        if (balance <= 50) {
            String sadFace = consoleService.readAsciiArtFromFile("C:/Users/" +
                    "Student/workspace/java-blue-module2capstone-team6/tenmo-client/src/main/resources/banners/sad.txt");
            System.out.print(sadFace + "  $");
            System.out.print(balance + "\n");
            System.out.println("___________________________");
        } else {
            String happyFace = consoleService.readAsciiArtFromFile("C:/Users/Student/workspace/java-blue-module2capstone-team6/tenmo-client/src/main/resources/banners/happy.txt");
            System.out.print(happyFace + "  $");
            System.out.print(balance + "\n");
            System.out.println("___________________________");
        }


    }

    private void viewTransferHistory() {
        Transfer[] pastTransfers = tenmoService.getTransferByUser();
        System.out.println("__________________________________");
        for (Transfer transfer : pastTransfers) {
            System.out.println(tenmoService.getTypeById(transfer.getTransfer_type_id()));
            System.out.println(tenmoService.getStatusById(transfer.getTransfer_status_id()));
            System.out.println(transfer.getAmount());
            System.out.println(transfer.getAccount_to());
            System.out.println(transfer.getAccount_from());


        }
//            System.out.println("__________________________________\n");
//            System.out.println(transfer);
//            System.out.println("__________________________________");
//        }
//        System.out.println("__________________________________");

    }

    private void viewPendingRequests() {
        Transfer[] pending = tenmoService.viewPending("Pending");
        System.out.println(pending.length);
        for (Transfer transfer : pending) {
            System.out.println(transfer);
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
            int option = consoleService.promptForInt("Press 1 to approve \nPress 2 to reject");
            TransferStatusDto updated = new TransferStatusDto();
            updated.setId(transfer.getTransfer_id());
            updated.setSendingAccount(transfer.getAccount_from());
            if (option == 1) {
                updated.setStatus("Approved");
                tenmoService.changeTransferStatus(updated);
                break;
            } else if (option == 2) {
                updated.setStatus("Rejected");
                tenmoService.changeTransferStatus(updated);
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
            int input = consoleService.promptForInt("Select user by account_id: ");
            accountToReceiveMoney = tenmoService.getAccountById(input);
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
            int input = consoleService.promptForInt("Select user by account_id: ");
            accountToReceiveMoney = tenmoService.getAccountById(input);
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
