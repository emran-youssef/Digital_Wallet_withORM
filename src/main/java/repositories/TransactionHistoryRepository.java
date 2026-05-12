package repositories;

import config.HibernateUtil;
import entities.TransactionHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class TransactionHistoryRepository {

    public void save(EntityManager em, TransactionHistory history) {
        em.persist(history);
    }

    public TransactionHistory findById(EntityManager em, Long id) {
        return em.find(TransactionHistory.class, id);
    }

    public TransactionHistory findByTransactionId(EntityManager em, Long transactionId) {
        try {
            return em.createQuery(
                            "SELECT h FROM TransactionHistory h WHERE h.transaction.id = :transactionId",
                            TransactionHistory.class
                    )
                    .setParameter("transactionId", transactionId)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public List<TransactionHistory> findByWalletId(EntityManager em, Long walletId) {
        return em.createQuery(
                        "SELECT h FROM TransactionHistory h WHERE h.wallet.id = :walletId",
                        TransactionHistory.class
                )
                .setParameter("walletId", walletId)
                .getResultList();
    }
}

