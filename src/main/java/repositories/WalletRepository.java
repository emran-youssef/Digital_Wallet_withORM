package repositories;

import config.HibernateUtil;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class WalletRepository {

        public void save(EntityManager em, Wallet wallet) {
            em.persist(wallet);
        }

        public Wallet findById(EntityManager em, Long id) {
            return em.find(Wallet.class, id);
        }

        public Wallet findByUserId(EntityManager em, Long userId) {

            try {

                return em.createQuery(
                                "SELECT w FROM Wallet w WHERE w.user.id = :userId",
                                Wallet.class
                        )
                        .setParameter("userId", userId)
                        .getSingleResult();

            } catch (NoResultException e) {
                return null;
            }
        }

        public List<Wallet> findAll(EntityManager em) {

            return em.createQuery(
                    "SELECT w FROM Wallet w",
                    Wallet.class
            ).getResultList();
        }

        public boolean existsByUserId(EntityManager em, Long userId) {
            Long count = em.createQuery(
                            "SELECT COUNT(w) FROM Wallet w WHERE w.user.id = :userId", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            return count > 0;
        }

        public Wallet update(EntityManager em, Wallet wallet) {
            return em.merge(wallet);
        }

        public void delete(EntityManager em, Wallet wallet) {

            if (!em.contains(wallet)) {
                wallet = em.merge(wallet);
            }

            em.remove(wallet);
        }
}

