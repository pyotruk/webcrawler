package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.threads.SavingThread;
import ru.webcrawler.threads.Task;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public final class CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    private static CrawlerService instance = null;

    private final ExecutorService pool;
    private final Thread savingThread;
    private final ConcurrentLinkedQueue<Page> toLoadQueue;
    private final ConcurrentLinkedQueue<Page> toSaveQueue;

    private int maxDepth;

    public ConcurrentLinkedQueue<Page> getToSaveQueue() {
        return toSaveQueue;
    }

    public ConcurrentLinkedQueue<Page> getToLoadQueue() {
        return toLoadQueue;
    }

    private CrawlerService(String url, int depth, int poolSize) {
        maxDepth = depth;
        toLoadQueue = new ConcurrentLinkedQueue<>();
        toSaveQueue = new ConcurrentLinkedQueue<>();
        pool = Executors.newFixedThreadPool(poolSize);
        savingThread = new SavingThread();

        // start page
        toLoadQueue.add(new Page(url, 0));
    }

    public static CrawlerService getInstance() {
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

        // start page
        Page page = toLoadQueue.poll();
        pool.execute(new Task(page, maxDepth));

        while ((((ThreadPoolExecutor) pool).getActiveCount() > 0) || (toLoadQueue.size() > 0) || (toSaveQueue.size() > 0)) {
            while ((page = toLoadQueue.poll()) != null) {
                pool.execute(new Task(page, maxDepth));
            }
        }

        log.info("SIZE(toLoadQueue)=" + toLoadQueue.size() + "; SIZE(toSaveQueue)=" + toSaveQueue.size());

        pool.shutdown();
        savingThread.interrupt();

        log.info("Completed.");
    }

}
