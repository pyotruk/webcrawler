package ru.webcrawler.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.CrawlerService;
import ru.webcrawler.Page;

import java.util.ArrayList;
import java.util.Queue;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class SavingThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SavingThread.class);

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                log.info("Interrupted.");
                break;
            }

            Queue<Page> queue = CrawlerService.getInstance().getLoadedQueue();
            ArrayList<Page> list = new ArrayList<>(queue);
            queue.clear();

            log.info(list.size() + " pages are saving...");
            for (Page page : list) {
                //TODO save to DB
            }
            log.info(list.size() + " pages have been saved.");
        }
    }
}
