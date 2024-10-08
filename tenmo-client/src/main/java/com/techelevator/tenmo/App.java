package com.techelevator.tenmo;

import com.techelevator.exceptions.LoginFailureException;
import com.techelevator.exceptions.TenmoRequestException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.model.dto.TransferDto;
import com.techelevator.tenmo.model.dto.TransferStatusDto;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class App extends JFrame {
    private final TenmoService tenmoService = new TenmoService();

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private Font unicode, boldText, pendingText;
    private JLabel faceLabel = new JLabel(), textLabel = new JLabel(), pendingLabel = new JLabel(),
            friendsConfirmLabel = new JLabel();

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
            friendsConfirmLabel.setText("");
            faceLabel.setText("╰(✿´⌣`✿)╯♡");

            // Set the font for both labels
            textLabel.setFont(boldText);
            pendingLabel.setFont(pendingText);
            friendsConfirmLabel.setFont(pendingText);
            faceLabel.setFont(unicode);


            // Center align the labels
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            pendingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            pendingLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // Top, Left, Bottom, Right
            friendsConfirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            faceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            faceLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Top, Left, Bottom, Right

            // Add the labels to the panel
            panel.add(Box.createVerticalGlue());
            panel.add(textLabel);
            panel.add(pendingLabel);
            panel.add(friendsConfirmLabel);
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
            friendsConfirmLabel.setText("Hmm, I don't recognise that user or password");
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
                textLabel.setText("Thanks for stopping by!");
                pendingLabel.setText("see you soon");
                friendsConfirmLabel.setText("");
                faceLabel.setText("d–(^ ‿ ^ )z");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                break;
            } else {
                System.out.println("Invalid Selection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            textLabel.setText("What would you like to do?");
            updatePendingLabel();
        }
    }

    private void viewCurrentBalance() {
        try {
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
        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
        consoleService.pause();
    }

    private void viewTransferHistory() {
        try {
            Transfer[] pastTransfers = tenmoService.getTransferByUser();
            int transferCount = pastTransfers.length;
            for (Transfer transfer : pastTransfers) {
                System.out.println(transfer);
            }
            textLabel.setText("You have made " + transferCount + " transfers");

            if (transferCount == 0) {
                pendingLabel.setText("It's time to connect with friends!");
                friendsConfirmLabel.setText("I'll always be here for you!");
            } else if (transferCount > 0 && transferCount < 11) {
                pendingLabel.setText("Good to see you connect with others!");
                friendsConfirmLabel.setText("xoxoxo");
            } else {
                pendingLabel.setText("My oh my!");
                friendsConfirmLabel.setText("you are very popular");
            }
            faceLabel.setText("(｡☉౪ ⊙｡)");


        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
        consoleService.pause();
    }

    private void viewPendingRequests() {
        // Get the list of pending transfers
        try {
            Transfer[] pendingRequests = tenmoService.viewPending();

            // Check if the list is empty
            if (pendingRequests.length == 0) {
                System.out.println("You are all caught up! No pending requests.");
                return; // Exit the method, returning to the main menu
            }

            // If there are pending requests, proceed with the rest of the method
            String message = "1) View requests to send money\n2) View requests to receive money";
            int choice = consoleService.promptForInt(message, 1, 2);

            int numPending = listPendingRequests(choice);
            if (numPending == 0) {
                return;
            }
            Transfer transfer = selectTransfer(choice);
            if (transfer != null) {
                finalizePending(transfer, choice);
            }
        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendBucks() {
        Account accountToReceiveMoney = chooseAccount();
        try {
            double balance = tenmoService.getAccountBalance();
            faceLabel.setText("( ๑ ❛ ڡ ❛ ๑ )❤");
            textLabel.setText("Available Balance: " + balance);
            pendingLabel.setText("How much should you send to " + accountToReceiveMoney.getUsername());
            friendsConfirmLabel.setText("");
            BigDecimal amount = consoleService.promptForBigDecimal("Select the amount to send: ");
            TransferDto transfer = new TransferDto();
            transfer.setAccount(accountToReceiveMoney.getAccount_id());
            transfer.setAmount(amount.doubleValue());
            String message = consoleService.promptForString("What's this for?: ");
            transfer.setMessage(message);
            tenmoService.sendTEBucksTo(transfer);
            faceLabel.setText("(っ˘з(˘⌣˘ )");
            textLabel.setText("Money Sent");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updatePendingLabel();
            balance = tenmoService.getAccountBalance();
            textLabel.setText("Available Balance: " + balance);
            faceLabel.setText("(｡☉౪ ⊙｡)");
            consoleService.pause();
        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void requestBucks() {
        try {
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            textLabel.setText("Available Balance: " + balance);
            faceLabel.setText("ʕಠಿ౪ಠʔ");
            consoleService.pause();
        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private Account chooseAccount() {
        boolean isValid = false;
        Account accountToReceiveMoney = null;

        while (!isValid) {
            System.out.println("  ___               ___  \n" +
                    " (o o)             (o o) \n" +
                    "(  V  ) \uD835\uDE4E\uD835\uDE5A\uD835\uDE61\uD835\uDE5A\uD835\uDE58\uD835\uDE69 \uD835\uDE50\uD835\uDE68\uD835\uDE5A\uD835\uDE67 (  V  )\n" +
                    "══m═m══════════════════m═m══\n");
            try {
                Account[] accounts = tenmoService.getAccounts();
                for (int i = 0; i < accounts.length; i++) {
                    System.out.println(i + 1 + ") " + accounts[i] + " -~··~-.¸.··._.·´¯·\n");

                }
                int input = consoleService.promptForInt("Enter Number: ", 1, accounts.length);

                accountToReceiveMoney = tenmoService.getUserAccount(accounts[input - 1].getUsername());
                currentUser.getUser().getUsername().equals(accountToReceiveMoney.getUsername());
                pendingLabel.setText("How much should " + accountToReceiveMoney.getUsername() + " send you?");
                friendsConfirmLabel.setText("");

                if (accountToReceiveMoney != null) {
                    isValid = true;
                }
            } catch (TenmoRequestException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        return accountToReceiveMoney;
    }


    private int listPendingRequests(int choice) {
        int numPending = 0;
        try {
            double balance = tenmoService.getAccountBalance();
            faceLabel.setText("乁( ⁰͡ Ĺ̯ ⁰͡ ) ㄏ");
            textLabel.setText("Available Balance: " + balance);
            Transfer[] pending = tenmoService.viewPending();
            if (choice == 1) {
                for (Transfer transfer : pending) {
                    if (transfer.getSender().getUsername().equals(currentUser.getUser().getUsername())) {
                        numPending++;
                        System.out.println(transfer);
                    }
                }
            } else {
                for (Transfer transfer : pending) {
                    if (transfer.getReceiver().getUsername().equals(currentUser.getUser().getUsername())) {
                        numPending++;
                        System.out.println(transfer);
                    }
                }
            }
        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
        return numPending;
    }


    private Transfer selectTransfer(int choice) {
        boolean isValid = false;
        Transfer transfer = null;
        String option = "";
        while (!isValid) {
            if (choice == 1) {
                option = consoleService.promptForString("Enter transaction id to change or press x to exit: ");
            } else {
                option = consoleService.promptForString("Enter transaction id to cxl or press x to exit: ");
            }
            if (option.equals("x")) {
                break;
            }
            int transactionIdToChange = 0;
            try {
                transactionIdToChange = Integer.parseInt(option);
                transfer = tenmoService.getTransferById(transactionIdToChange);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
                continue;
            } catch (TenmoRequestException e) {
                System.out.println(e.getMessage());
                return null;
            }
            if (transfer != null) {
                isValid = true;
            }
        }
        return transfer;
    }

    private void finalizePending(Transfer transfer, int choice) {
        double balance;
        TransferStatusDto updated = new TransferStatusDto();
        boolean isValid = false;
        while (!isValid) {
            String amount = String.valueOf(transfer.getAmount());
            pendingLabel.setText("Send $" + amount + " to " + transfer.getReceiver().getUsername() + " ?");
            friendsConfirmLabel.setText("");
            updated.setId(transfer.getTransfer_id());
            if (choice == 1) {
                int option = consoleService.promptForInt("Press 1 to approve \nPress 2 to reject: ", 1, 2);

                if (option == 1) {
                    updated.setStatus("Approved");
                    try {
                        tenmoService.changeTransferStatus(updated);
                        faceLabel.setText("╰( ⁰ ਊ ⁰ )━☆ﾟ.*･｡ﾟ");
                        pendingLabel.setText("Money Sent!");
                        balance = tenmoService.getAccountBalance();
                        textLabel.setText("Available Balance: " + balance);
                        resetPanel();
                        consoleService.pause();
                    } catch (TenmoRequestException e) {
                        System.out.println(e.getMessage());
                        faceLabel.setText("┌( ◕ 益 ◕ )ᓄ");
                        resetPanel();
                    }
                    break;
                } else if (option == 2) {
                    updated.setStatus("Rejected");
                    try {
                        tenmoService.changeTransferStatus(updated);
                        pendingLabel.setText("Transfer Rejected");
                        faceLabel.setText("'''⌐(ಠ۾ಠ)¬'''");
                        resetPanel();
                    } catch (TenmoRequestException e) {
                        System.out.println(e.getMessage());
                        faceLabel.setText("┌( ◕ 益 ◕ )ᓄ");
                    }
                    break;
                }
            } else {
                updated.setStatus("Canceled");
                try {
                    tenmoService.changeTransferStatus(updated);
                    pendingLabel.setText("Transfer Canceled");
                    faceLabel.setText("༼ つ ಥ_ಥ ༽つ");
                    resetPanel();
                } catch (TenmoRequestException e) {
                    System.out.println(e.getMessage());
                    faceLabel.setText("┌( ◕ 益 ◕ )ᓄ");
                    resetPanel();
                }
            }
            isValid = true;
        }
    }

    private void updatePendingLabel() {
        try {
            Transfer[] pendingRequests = tenmoService.viewPending();
            int numRequestsOfUser = 0;
            int numRequestsFromUser = 0;
            for (Transfer transfer : pendingRequests) {
                if (transfer.getSender().getUsername().equals(currentUser.getUser().getUsername())) {
                    numRequestsOfUser++;
                }
                if (transfer.getReceiver().getUsername().equals(currentUser.getUser().getUsername())) {
                    numRequestsFromUser++;
                }
            }
            String numberPending = String.valueOf(numRequestsOfUser);
            String requestsOutToOthers = String.valueOf(numRequestsFromUser);

            if (numRequestsOfUser == 0) {
                pendingLabel.setText("You are all caught up! No one is asking for money!");
            } else {
                pendingLabel.setText("You have " + numberPending + " pending requests");
            }
            if (numRequestsFromUser == 0) {
                friendsConfirmLabel.setText("Your friends have processed all of your requests!");
            } else {
                friendsConfirmLabel.setText("Friends have not decided on " + requestsOutToOthers + " of your requests");
            }
        } catch (TenmoRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void resetPanel() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updatePendingLabel();
        faceLabel.setText("(♡´౪`♡)");
    }
}

