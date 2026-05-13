import config.HibernateUtil;
import entities.User;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import menu.ConsoleMenu;
import repositories.TransactionHistoryRepository;
import repositories.TransactionRepository;
import repositories.UserRepository;
import repositories.WalletRepository;
import service.TransactionHistoryService;
import service.TransactionService;
import service.UserService;
import service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args){

        // Repositories
        UserRepository userRepo    = new UserRepository();
        WalletRepository walletRepo  = new WalletRepository();
        TransactionRepository txRepo      = new TransactionRepository();
        TransactionHistoryRepository historyRepo = new TransactionHistoryRepository();


        // Services
        UserService userService    = new UserService(userRepo);
        WalletService walletService  = new WalletService(walletRepo);
        TransactionHistoryService historyService = new TransactionHistoryService(historyRepo);
        TransactionService txService      = new TransactionService(walletService, txRepo, historyService);


        // Launch
        new ConsoleMenu(userService, walletService, txService, historyService).start();

        // Cleanup
        HibernateUtil.shutDown();


    }

}







/*
        test code:

        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            User user3 = User.builder()
                    .username("test6user")
                    .email("test6@test.com")
                    .password("1234")
                    .createdAt(LocalDateTime.now())
                    .build();

            em.persist(user3);

            Wallet wallet2 = Wallet.builder()
                    .balance(BigDecimal.ZERO)
                    .user(user3)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            em.persist(wallet2);

            user3.setWallet(wallet2);

            tx.commit();
            System.out.println("Saved successfully — User ID: " + user3.getId());
            System.out.println("Saved successfully — Wallet ID: " + wallet2.getId());

            // retrieve and verify
            User found = em.find(User.class, user3.getId());
            System.out.println("Retrieved user: " + found.getUsername());

            System.out.println("Retrieved wallet balance: " + found.getWallet().getBalance());

        } catch (Exception e) {
            if (tx.isActive()) {  // only rollback if transaction is still active
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }


         */
