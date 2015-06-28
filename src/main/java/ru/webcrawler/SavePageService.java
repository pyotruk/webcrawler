package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class SavePageService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SavePageService.class);

    private static SavePageService instance;

    private final static long SLEEP = 5000;

    private final Queue<Page> queue;
    private List<Page> pagesToSave = new ArrayList<>();
    private final EntityManager entityManager;

    private SavePageService(Queue<Page> toSaveQueue) {
        this.queue = toSaveQueue;
        this.entityManager = EntityManagerUtil.getEntityManagerFactory().createEntityManager();
        log.info("Created.");
    }

    public static SavePageService createInstance(Queue<Page> toSaveQueue) {
        if (instance == null) {
            instance = new SavePageService(toSaveQueue);
            return instance;

        } else {
            throw new IllegalStateException("Service is a singleton and it is already created.");
        }
    }

    private void saveToDB() {
        EntityTransaction t = entityManager.getTransaction();

        t.begin();
        for (Page page : pagesToSave) entityManager.persist(page);
        t.commit();

        log.info("{} pages have been saved.", pagesToSave.size());
        pagesToSave.clear();
    }

    private void doSave() {
        Page page;
        while ((page = queue.poll()) != null) pagesToSave.add(page);
        saveToDB();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                sleep(SLEEP);
                doSave();

            } catch (InterruptedException e) {
                doSave();
                log.info(e.getMessage());
                break;
            }
        }
        entityManager.close();
        //TODO check interruption
    }
}
