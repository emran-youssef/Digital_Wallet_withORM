package menu;

import config.HibernateUtil;
import entities.TransactionHistory;
import entities.User;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import service.TransactionHistoryService;
import service.TransactionService;
import service.UserService;
import service.WalletService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);

    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final TransactionHistoryService transactionHistoryService;

    public ConsoleMenu(
            UserService userService,
            WalletService walletService,
            TransactionService transactionService,
            TransactionHistoryService transactionHistoryService) {

        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.transactionHistoryService = transactionHistoryService;
    }

    // Session state — null means no one is logged in
    private User currentUser = null;



    //  Entry point
    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleRegister();
                case "2" -> handleLogin();
                case "3" -> handleDeposit();
                case "4" -> handleTransfer();
                case "5" -> handleWithdraw();
                case "6" -> handleCheckBalance();
                case "7" -> handleViewHistory();
                case "0" -> {
                    System.out.println("\nGoodbye!\n");
                    running = false;
                }
                default -> System.out.println("\n[!] Invalid option. Please enter a number from the menu.\n");
            }
        }
    }


    //  Menu display
    private void printBanner() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       Digital Wallet System          ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
    }

    private void printMenu() {
        System.out.println("──────────────────────────────────────");
        if (currentUser != null) {
            System.out.println("  Logged in as: " + currentUser.getUsername());
        } else {
            System.out.println("  Not logged in");
        }
        System.out.println("──────────────────────────────────────");
        System.out.println("  1. Register");
        System.out.println("  2. Login");
        System.out.println("  3. Add money (deposit)");
        System.out.println("  4. Send money (transfer)");
        System.out.println("  5. Withdraw money");
        System.out.println("  6. Check balance");
        System.out.println("  7. View transaction history");
        System.out.println("  0. Exit");
        System.out.println("──────────────────────────────────────");
        System.out.print("  Choice: ");
    }


    //  Handlers
    private void handleRegister() {
        System.out.println("\n── Register ──");

        String username = prompt("  Username: ");
        String email    = prompt("  Email: ");
        String password = prompt("  Password: ");

        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            User user = userService.register(em, username, email, password);
            walletService.createWallet(em, user);

            em.getTransaction().commit();
            System.out.println("\n[✓] Registration successful! Welcome, " + user.getUsername() + ".\n");

        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            printError(e.getMessage());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            printError("Unexpected error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private void handleLogin() {
        System.out.println("\n── Login ──");

        String username = prompt("  Username: ");
        String password = prompt("  Password: ");

        EntityManager em = HibernateUtil.getEntityManager();
        try {
            boolean success = userService.login(em, username, password);

            if (success) {
                currentUser = userService.findByUsername(em, username);
                System.out.println("\n[✓] Login successful! Welcome back, " + currentUser.getUsername() + ".\n");
            } else {
                printError("Invalid username or password.");
            }

        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private void handleDeposit() {
        if (!requireLogin()) return;

        System.out.println("\n── Add Money (Deposit) ──");

        BigDecimal amount = promptAmount("  Amount to deposit: ");
        if (amount == null) return;

        try {
            transactionService.deposit(currentUser.getId(), amount);
            System.out.printf("%n[✓] Successfully deposited $%.2f to your wallet.%n%n", amount);

        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleTransfer() {
        if (!requireLogin()) return;

        System.out.println("\n── Send Money (Transfer) ──");

        String recipientUsername = prompt("  Recipient username: ");
        BigDecimal amount = promptAmount("  Amount to send: ");
        if (amount == null) return;

        // Resolve recipient username → userId
        EntityManager em = HibernateUtil.getEntityManager();
        Long recipientId;
        try {
            User recipient = userService.findByUsername(em, recipientUsername);
            recipientId = recipient.getId();
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
            return;
        } finally {
            em.close();
        }

        try {
            transactionService.transfer(currentUser.getId(), recipientId, amount);
            System.out.printf("%n[✓] Successfully sent $%.2f to %s.%n%n", amount, recipientUsername);

        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleWithdraw() {
        if (!requireLogin()) return;

        System.out.println("\n── Withdraw Money ──");

        BigDecimal amount = promptAmount("  Amount to withdraw: ");
        if (amount == null) return;

        try {
            transactionService.withdraw(currentUser.getId(), amount);
            System.out.printf("%n[✓] Successfully withdrew $%.2f from your wallet.%n%n", amount);

        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleCheckBalance() {
        if (!requireLogin()) return;

        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Wallet wallet = walletService.findWalletByUserId(em, currentUser.getId());
            System.out.printf("%n  Current balance: $%.2f%n%n", wallet.getBalance());

        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        } finally {
            em.close();
        }
    }

    private void handleViewHistory() {
        if (!requireLogin()) return;

        EntityManager em = HibernateUtil.getEntityManager();
        Long walletId;
        try {
            Wallet wallet = walletService.findWalletByUserId(em, currentUser.getId());
            walletId = wallet.getId();
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
            return;
        } finally {
            em.close();
        }

        List<TransactionHistory> history = transactionHistoryService.getWalletHistory(walletId);

        System.out.println("\n── Transaction History ──");
        if (history == null || history.isEmpty()) {
            System.out.println("  No transactions found.\n");
            return;
        }

        System.out.println();
        System.out.printf("  %-5s %-12s %-12s %-10s %s%n",
                "#", "Type", "Status", "Amount", "Date");
        System.out.println("  " + "─".repeat(60));

        int index = 1;
        for (TransactionHistory h : history) {
            System.out.printf("  %-5d %-12s %-12s $%-9.2f %s%n",
                    index++,
                    h.getType(),
                    h.getStatus(),
                    h.getAmount(),
                    h.getArchivedAt().toLocalDate());
        }
        System.out.println();
    }

    //Helpers:
    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private BigDecimal promptAmount(String message) {
        String input = prompt(message);
        try {
            BigDecimal value = new BigDecimal(input);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                printError("Amount must be greater than zero.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            printError("Invalid amount. Please enter a numeric value (e.g. 50 or 50.00).");
            return null;
        }
    }

    private boolean requireLogin() {
        if (currentUser == null) {
            printError("You must be logged in to perform this action.");
            return false;
        }
        return true;
    }

    private void printError(String message) {
        System.out.println("\n[✗] " + message + "\n");
    }
}



