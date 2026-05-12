package repositories;

import config.HibernateUtil;
import entities.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class WalletRepository {

    public void saveWallet(Wallet wallet){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(wallet);
            em.getTransaction().commit();

        } catch (Exception e) {
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        }finally {
            em.close();
        }
    }

    public Wallet findById(Long id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.find(Wallet.class, id);
        } finally {
            em.close();
        }
    }

    public Wallet findByUserId(Long userId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT w FROM Wallet w WHERE w.user.id = :userId", Wallet.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void update(Wallet wallet) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(wallet);
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

}
