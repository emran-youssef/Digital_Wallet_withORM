package repositories;

import entities.Transaction;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TransactionRepository {

    public void save(EntityManager em, Transaction transaction) {
        em.persist(transaction);
    }

    //future use:
    public Transaction findById(EntityManager em, Long id) {
        return em.find(Transaction.class, id);
    }


    public List<Transaction> findByWalletId(
            EntityManager em,
            Long walletId
    ) {
        return em.createQuery(
                        "SELECT t FROM Transaction t " +
                                "WHERE t.sender.id = :walletId OR t.receiver.id = :walletId",
                        Transaction.class
                )
                .setParameter("walletId", walletId)
                .getResultList();
    }
}