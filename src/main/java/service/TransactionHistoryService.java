package service;

import config.HibernateUtil;
import entities.Transaction;
import entities.TransactionHistory;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import repositories.TransactionHistoryRepository;

import java.util.List;

@AllArgsConstructor
public class TransactionHistoryService {

        private final TransactionHistoryRepository historyRepository;

        public void createHistory(EntityManager em,Transaction transaction, Wallet wallet) {


            try {
                em.getTransaction().begin();

                TransactionHistory history = TransactionHistory.builder()
                        .transaction(transaction)
                        .wallet(wallet)
                        .build();

                historyRepository.save(em, history);
                em.getTransaction().commit();

            } catch (Exception e) {
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                throw e;

            } finally {
                em.close();
            }
        }

    public TransactionHistory getByTransactionId(Long transactionId) {

        EntityManager em = HibernateUtil.getEntityManager();

        try {
            return historyRepository.findByTransactionId(em, transactionId);

        } finally {
            em.close();
        }
    }

    public List<TransactionHistory> getWalletHistory(Long walletId) {

        EntityManager em = HibernateUtil.getEntityManager();

        try {
            return historyRepository.findByWalletId(em, walletId);

        } finally {
            em.close();
        }
    }
}
