package repositories;

import config.HibernateUtil;
import entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UserRepository {

        public void save(EntityManager em, User user) {
            em.persist(user);
        }

        public User findById(EntityManager em, Long id) {
            return em.find(User.class, id);
        }

        public User findByUsername(EntityManager em, String username) {
            try {
                return em.createQuery(
                                "SELECT u FROM User u WHERE u.username = :username",
                                User.class
                        )
                        .setParameter("username", username)
                        .getSingleResult();

            } catch (NoResultException e) {
                return null;
            }
        }

        public User findByEmail(EntityManager em, String email) {
            try {
                return em.createQuery(
                                "SELECT u FROM User u WHERE u.email = :email",
                                User.class
                        )
                        .setParameter("email", email)
                        .getSingleResult();

            } catch (NoResultException e) {
                return null;
            }
        }

        public boolean existsByUsername(EntityManager em, String username) {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                            Long.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();

            return count > 0;
        }

        public boolean existsByEmail(EntityManager em, String email) {
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                            Long.class
                    )
                    .setParameter("email", email)
                    .getSingleResult();

            return count > 0;
        }
    }


