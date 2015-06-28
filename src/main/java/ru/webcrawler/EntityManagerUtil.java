package ru.webcrawler;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * User: pyotruk
 * Date: 2015-06-28
 */

public class EntityManagerUtil {

    public static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2");
        return emf;
    }

}
