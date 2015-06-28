package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.threads.SavePageService;
import ru.webcrawler.threads.LoadPageTask;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public final class CrawlerService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    private static CrawlerService instance = null;

    private final ExecutorService pool;
    private final SavePageService savePageService;
    private final ConcurrentLinkedQueue<Page> toLoadQueue;
    private final ConcurrentLinkedQueue<Page> toSaveQueue;

    private final int maxDepth;

    private CrawlerService(String url, int depth, int poolSize) {
        maxDepth = depth;
        toLoadQueue = new ConcurrentLinkedQueue<>();
        toSaveQueue = new ConcurrentLinkedQueue<>();
        pool = Executors.newFixedThreadPool(poolSize);
        savePageService = SavePageService.createInstance(toSaveQueue);

        // start page
        toLoadQueue.add(new Page(url, 0));

        log.info("Created with params: [url:{}][depth:{}][poolSize:{}]", url, depth, poolSize);
    }

    public static CrawlerService createInstance(String url, int depth, int poolSize) {
        if (instance == null) {
            instance = new CrawlerService(url, depth, poolSize);
            return instance;

        } else {
            throw new IllegalStateException("Service is a singleton and it is already created.");
        }
    }

    @Override
    public void run() {
        log.info("Starting...");

        // start page
        Page page = toLoadQueue.poll();
        pool.execute(buildTask(page));

        savePageService.start();

        while (!isCompleted()) {
            while ((page = toLoadQueue.poll()) != null) {
                pool.execute(buildTask(page));
            }
        }

        log.info("SIZE(toLoadQueue)={}; SIZE(toSaveQueue)={}", toLoadQueue.size(), toSaveQueue.size());

        pool.shutdown();
        savePageService.interrupt();

        log.info("Completed.");
    }

    private LoadPageTask buildTask(Page page) {
        return new LoadPageTask(page, maxDepth, toLoadQueue, toSaveQueue);
    }

    private boolean isCompleted() {
        return (((ThreadPoolExecutor) pool).getActiveCount() == 0) && (toLoadQueue.size() == 0) && (toSaveQueue.size() == 0);
    }

}
