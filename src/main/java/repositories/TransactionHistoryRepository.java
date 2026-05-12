package repositories;

import config.HibernateUtil;
import entities.TransactionHistory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TransactionHistoryRepository {

    public void save(TransactionHistory history) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(history);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public TransactionHistory findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(TransactionHistory.class, id);
        } finally {
            em.close();
        }
    }

    public TransactionHistory findByTransactionId(Long transactionId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT h FROM TransactionHistory h WHERE h.transaction.id = :transactionId",
                            TransactionHistory.class)
                    .setParameter("transactionId", transactionId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<TransactionHistory> findByWalletId(Long walletId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT h FROM TransactionHistory h WHERE h.wallet.id = :walletId",
                            TransactionHistory.class)
                    .setParameter("walletId", walletId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

