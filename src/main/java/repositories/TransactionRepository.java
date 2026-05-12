package repositories;

import config.HibernateUtil;
import entities.Transaction;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TransactionRepository {

    public void save(Transaction transaction) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(transaction);
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

    public Transaction findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Transaction.class, id);
        } finally {
            em.close();
        }
    }

    public void update(Transaction transaction) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(transaction);
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

    public List<Transaction> findBySenderWalletId(Long walletId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t WHERE t.sender.id = :walletId", Transaction.class)
                    .setParameter("walletId", walletId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Transaction> findByReceiverWalletId(Long walletId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.receiver.id = :walletId", Transaction.class)
                    .setParameter("walletId", walletId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
