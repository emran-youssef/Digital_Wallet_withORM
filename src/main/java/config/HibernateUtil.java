package config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.flywaydb.core.Flyway;

public class HibernateUtil {

    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    //  buildEntityManagerFactory: create the entity manager
    private static EntityManagerFactory buildEntityManagerFactory(){
        try {
            //to run the migrations automatically on the start-up
            runFlywayMigrations();

            //createEntityManagerFactory: reads the persistence.xml file and connect to the db.
            return Persistence.createEntityManagerFactory("digital-wallet");

        } catch (Exception e) {
            System.err.println("Failed to create EntityManagerFactory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    // returning entity manger to use it to run queries, used in the repositories.
    public static EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    public static void shutDown(){
        if(emf != null && emf.isOpen()){
            emf.close();
        }
    }

    private static void runFlywayMigrations() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:mysql://localhost:3306/wallet_db",
                        "root",
                        "0000"
                )
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
    }
}
