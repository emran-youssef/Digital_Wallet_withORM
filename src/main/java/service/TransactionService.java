package service;

import config.HibernateUtil;
import entities.Transaction;
import entities.Wallet;
import enums.TransactionStatus;
import enums.TransactionType;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import repositories.TransactionRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class TransactionService {

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final TransactionHistoryService transactionHistoryService;


    public Transaction deposit(Long userId, BigDecimal amount) {

        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Wallet wallet = walletService.findWalletByUserId(em, userId);
            walletService.deposit(wallet, amount);
            em.merge(wallet); // persist balance change

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .type(TransactionType.DEPOSIT)
                    .status(TransactionStatus.COMPLETED)
                    .sender(wallet)
                    .receiver(wallet)
                    .createdAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(em, transaction);
            em.getTransaction().commit();
            transactionHistoryService.createHistory(transaction, wallet);
            return transaction;

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Transaction withdraw(Long userId, BigDecimal amount) {

        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Wallet wallet = walletService.findWalletByUserId(em, userId);
            walletService.withdraw(wallet, amount);

            em.merge(wallet); // persist the balance change

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .type(TransactionType.WITHDRAW)
                    .status(TransactionStatus.COMPLETED)
                    .sender(wallet)
                    .receiver(wallet)
                    .createdAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(em, transaction);
            em.getTransaction().commit();

            transactionHistoryService.createHistory(transaction, wallet);

            return transaction;

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Transaction transfer(Long senderId, Long receiverId, BigDecimal amount){

        if (senderId.equals(receiverId))
            throw new IllegalArgumentException("Cannot transfer to the same wallet");

        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();

            Wallet senderWallet = walletService.findWalletByUserId(em, senderId);
            Wallet receiverWallet = walletService.findWalletByUserId(em, receiverId);

            walletService.withdraw(senderWallet, amount);
            walletService.deposit(receiverWallet, amount);
            em.merge(senderWallet);   // persist balance change
            em.merge(receiverWallet); // persist balance change

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .type(TransactionType.TRANSFER)
                    .status(TransactionStatus.COMPLETED)
                    .sender(senderWallet)
                    .receiver(receiverWallet)
                    .createdAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(em, transaction);
            em.getTransaction().commit();

            transactionHistoryService.createHistory(transaction, senderWallet);
            transactionHistoryService.createHistory(transaction, receiverWallet);

            return transaction;

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        }

    }

