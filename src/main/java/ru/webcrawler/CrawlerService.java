package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.threads.SavingThread;
import ru.webcrawler.threads.Task;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public final class CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    private static CrawlerService instance = null;

    private ExecutorService executorService;
    private Thread savingThread;
    private ConcurrentLinkedQueue<Page> toLoadQueue;
    private ConcurrentLinkedQueue<Page> loadedQueue;

    private int maxDepth;

    public ConcurrentLinkedQueue<Page> getLoadedQueue() {
        return loadedQueue;
    }

    public ConcurrentLinkedQueue<Page> getToLoadQueue() {
        return toLoadQueue;
    }

    private CrawlerService(String url, int depth, int poolSize) {
        maxDepth = depth;
        toLoadQueue = new ConcurrentLinkedQueue<>();
        loadedQueue = new ConcurrentLinkedQueue<>();
        executorService = Executors.newFixedThreadPool(poolSize);
        savingThread = new SavingThread();

        // start page
        toLoadQueue.add(new Page(url, 0));
    }

    synchronized public static CrawlerService getInstance() {
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("You must start service at first.");
        }
    }

    synchronized public static void start(String url, int depth, int poolSize) {
        if (instance == null) {
            instance = new CrawlerService(url, depth, poolSize);
            instance.run();

        } else {
            throw new IllegalStateException("Service already started.");
        }
    }

    private void run() {
        log.info("Starting...");

        savingThread.start();

        while (!toLoadQueue.isEmpty() || !loadedQueue.isEmpty()) {
            log.info("SIZE(toLoadQueue)=" + toLoadQueue.size() + "; SIZE(loadedQueue)=" + loadedQueue.size());

            Page page = toLoadQueue.poll();
            executorService.execute(new Task(page, maxDepth));
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                break;
            }
        }

        executorService.shutdown();
        savingThread.interrupt();

        log.info("Completed.");
    }

}
