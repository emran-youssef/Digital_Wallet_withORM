import config.HibernateUtil;
import entities.User;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args){

        /*
        System.out.println("Starting Digital Wallet...");
        EntityManager em = HibernateUtil.getEntityManager();
        System.out.println("Database connection successful!");
        em.close();

        HibernateUtil.shutDown();
        System.out.println("Shutdown complete.");
         */

        EntityManager em = HibernateUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            User user2 = User.builder()
                    .username("test5user")
                    .email("test5@test.com")
                    .password("1234")
                    .createdAt(LocalDateTime.now())
                    .build();

            em.persist(user2);

            Wallet wallet1 = Wallet.builder()
                    .balance(BigDecimal.ZERO)
                    .user(user2)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            em.persist(wallet1);

            user2.setWallet(wallet1);

            tx.commit();
            System.out.println("Saved successfully — User ID: " + user2.getId());
            System.out.println("Saved successfully — Wallet ID: " + wallet1.getId());

            // retrieve and verify
            User found = em.find(User.class, user2.getId());
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

    }
}
