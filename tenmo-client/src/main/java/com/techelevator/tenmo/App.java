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

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class App extends JFrame {
    private final TenmoService tenmoService = new TenmoService();

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private Font unicode;
    private Font boldText;
    private Font pendingText;
    private JLabel faceLabel = new JLabel();
    private JLabel textLabel = new JLabel();
    private JLabel pendingLabel = new JLabel();

    public App() {
        setTitle("TEnmo Companion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        try {
            // Load the font file
            InputStream textFontStream = App.class.getResourceAsStream("/fonts/Montserrat-ExtraBoldItalic.ttf");
            boldText = Font.createFont(Font.TRUETYPE_FONT, textFontStream).deriveFont(30f);

            InputStream fontStream = App.class.getResourceAsStream("/fonts/arial-unicode-ms.ttf");
            unicode = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(70f);

            InputStream pendingFontStream = App.class.getResourceAsStream("/fonts/Montserrat-Light.ttf");
            pendingText = Font.createFont(Font.TRUETYPE_FONT, pendingFontStream).deriveFont(20f);

            // Create a JPanel with a vertical BoxLayout
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            // Set the background color
            panel.setBackground(new Color(255, 43, 136, 255));
            panel.setOpaque(true);


            // Create JLabels for the face and the text
            textLabel.setText("Welcome to TEnmo friend");
            pendingLabel.setText("");
            faceLabel.setText("╰(✿´⌣`✿)╯♡");

            // Set the font for both labels
            textLabel.setFont(boldText);
            pendingLabel.setFont(pendingText);
            faceLabel.setFont(unicode);


            // Center align the labels
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            pendingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            pendingLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // Top, Left, Bottom, Right
            faceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            faceLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Top, Left, Bottom, Right

            // Add the labels to the panel
            panel.add(Box.createVerticalGlue());
            panel.add(textLabel);
            panel.add(pendingLabel);
            panel.add(faceLabel);
            panel.add(Box.createVerticalGlue());

            // Add the panel to the frame
            add(panel, BorderLayout.CENTER);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.setVisible(true);
        app.run();
    }

    private void run() {
        String TEnmo = consoleService.readAsciiArtFromFile("src/main/resources/banner/banner.txt");
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
        updatePendingLabel();

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
        double balance = tenmoService.getAccountBalance();
        textLabel.setText("Available Balance: " + balance);
        System.out.println("\n     ＄");
        System.out.println("   ▂█▀█▂▂ ▀█▀ ▐⯊ █▚▌ █▚▞▌ ⬤ ▂▂    ");
        System.out.println("  █▀█▀█▀█══════Balance═════█████");
        System.out.println(" ▄█▄█▄█▄█▄" + "            " + "     █████");
        System.out.print("▄█▄██\uD83C\uDF9B██▄█▄ " + "    $" + balance + " " + "      ██");
        System.out.println("\n                 _____\n" +
                "            //  /_..._\\    //\n" +
                "           //  (0[###]0)  //\n" +
                "          //    ''   ''  // ");
    }

    private void viewTransferHistory() {
        Transfer[] pastTransfers = tenmoService.getTransferByUser();
        for (Transfer transfer : pastTransfers) {
            System.out.println(transfer);
            textLabel.setText("You have made " + pastTransfers.length + " transfers");
            faceLabel.setText("(｡☉౪ ⊙｡)");
        }


    }

    private void viewPendingRequests() {
        // Get the list of pending transfers
        Transfer[] pendingRequests = tenmoService.viewPending();

        // Check if the list is empty
        if (pendingRequests.length == 0) {
            System.out.println("You are all caught up! No pending requests.");
            return; // Exit the method, returning to the main menu
        }

        // If there are pending requests, proceed with the rest of the method
        listPendingRequests();
        Transfer transfer = selectTransfer();
        if (transfer != null) {
            finalizePending(transfer);
        }
    }

    private void sendBucks() {
        Account accountToReceiveMoney = chooseAccount();
        double balance = tenmoService.getAccountBalance();
        faceLabel.setText("( ๑ ❛ ڡ ❛ ๑ )❤");
        textLabel.setText("Available Balance: " + balance);

        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to send: ");
        TransferDto transfer = new TransferDto();
        transfer.setAccount(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.doubleValue());
        String message = consoleService.promptForString("What's this for?: ");
        transfer.setMessage(message);
        try {
            tenmoService.sendTEBucksTo(transfer);
            faceLabel.setText("(っ˘з(˘⌣˘ )");
            textLabel.setText("Money Sent");
            consoleService.pause();
            balance = tenmoService.getAccountBalance();
            textLabel.setText("Available Balance: " + balance);
            faceLabel.setText("(｡☉౪ ⊙｡)");
        } catch (InsufficientFunds e) {
            System.out.println(e.getMessage());
        }
    }


    private void requestBucks() {
        Account accountToReceiveMoney = chooseAccount();
        double balance = tenmoService.getAccountBalance();
        faceLabel.setText("♥(ˆ⌣ˆԅ)");
        textLabel.setText("Available Balance: " + balance);
        BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to receive: ");
        TransferDto transfer = new TransferDto();
        transfer.setAccount(accountToReceiveMoney.getAccount_id());
        transfer.setAmount(amount.doubleValue());
        String message = consoleService.promptForString("What's this for?: ");
        transfer.setMessage(message);
        tenmoService.requestTEBucksFrom(transfer);
        faceLabel.setText("('_')┏oo┓('_')");
        updatePendingLabel();
        textLabel.setText("Request Sent");
        consoleService.pause();
        textLabel.setText("Available Balance: " + balance);
        faceLabel.setText("ʕಠಿ౪ಠʔ");
    }

    private Account chooseAccount() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;
        while (!isValid) {
            System.out.println("  ___               ___  \n" +
                    " (o o)             (o o) \n" +
                    "(  V  ) \uD835\uDE4E\uD835\uDE5A\uD835\uDE61\uD835\uDE5A\uD835\uDE58\uD835\uDE69 \uD835\uDE50\uD835\uDE68\uD835\uDE5A\uD835\uDE67 (  V  )\n" +
                    "══m═m══════════════════m═m══\n");

            Account[] accounts = tenmoService.getAccounts();
            for (int i = 0; i < accounts.length; i++) {
                System.out.println(i + 1 + ") " + accounts[i] + " -~··~-.¸.··._.·´¯·\n");

            }
            int input = consoleService.promptForInt("Enter Number: ");
            if (input > accounts.length || input < 1) {
                System.out.println("please enter a valid index");
                continue;
            }
            accountToReceiveMoney = tenmoService.getUserAccount(accounts[input - 1].getUsername());
            if (accountToReceiveMoney != null) {
                isValid = true;
            }
        }
        return accountToReceiveMoney;
    }

    private void listPendingRequests() {
        double balance = tenmoService.getAccountBalance();
        faceLabel.setText("乁( ⁰͡ Ĺ̯ ⁰͡ ) ㄏ");
        textLabel.setText("Available Balance: " + balance);
        Transfer[] pending = tenmoService.viewPending();
        System.out.println(pending.length);
        for (Transfer transfer : pending) {
            System.out.println(transfer);
        }


    }

    private Transfer selectTransfer() {
        boolean isValid = false;
        Transfer transfer = null;
        while (!isValid) {

            String option = consoleService.promptForString("Enter transaction id to change or hit x to exit: ");
            if (option.equals("x")) {
                break;
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
        return transfer;
    }

    private void finalizePending(Transfer transfer) {
        double balance = tenmoService.getAccountBalance();
        boolean isValid = false;
        while (!isValid) {
            String amount = String.valueOf(transfer.getAmount());
            pendingLabel.setText("Send $" + amount + " ?");
            int option = consoleService.promptForInt("Press 1 to approve \nPress 2 to reject: ");
            TransferStatusDto updated = new TransferStatusDto();
            updated.setId(transfer.getTransfer_id());
            updated.setSendingAccount(transfer.getSender().getAccount_id());
            if (option == 1) {
                updated.setStatus("Approved");
                try {
                    tenmoService.changeTransferStatus(updated);
                    faceLabel.setText("╰( ⁰ ਊ ⁰ )━☆ﾟ.*･｡ﾟ");
                    updatePendingLabel();
                    balance = tenmoService.getAccountBalance();
                    textLabel.setText("Available Balance: " + balance);
                    consoleService.pause();
                    faceLabel.setText("(♡´౪`♡)");


                } catch (RestClientException e) {
                    System.out.println("User cannot approve their own requests");
                    faceLabel.setText("┌( ◕ 益 ◕ )ᓄ");

                }
                break;
            } else if (option == 2) {
                updated.setStatus("Rejected");
                try {
                    tenmoService.changeTransferStatus(updated);
                    updatePendingLabel();
                    faceLabel.setText("'''⌐(ಠ۾ಠ)¬'''");
                } catch (RestClientException e) {
                    System.out.println("User cannot Reject their own requests");
                    faceLabel.setText("┌( ◕ 益 ◕ )ᓄ");
                }
                break;
            } else {
                System.out.println("Please enter a valid option");
                faceLabel.setText("(΄◞ิ౪◟ิ‵)");
            }
        }
    }

    private void updatePendingLabel() {
        Transfer[] pendingRequests = tenmoService.viewPending();
        String numberPending = String.valueOf(pendingRequests.length);
        if (numberPending.equals(0)) {
            pendingLabel.setText("You are all caught up! No pending requests.");
        } else {
            pendingLabel.setText("You have " + numberPending + " pending requests");
        }
    }
}
