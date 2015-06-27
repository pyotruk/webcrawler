package ru.webcrawler.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.CrawlerService;
import ru.webcrawler.Page;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class Task extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Task.class);

    private Page page;
    private int maxDepth;

    public Task(Page page, int maxDepth) {
        this.page = page;
        this.maxDepth = maxDepth;
    }

    @Override
    public void run() {
        if (page.getDepth() > maxDepth) {
            //log.info("MaxDepth (" + maxDepth + ") has reached.");
            return;
        }
        page.load();
        CrawlerService.getInstance().getToSaveQueue().offer(page);

        ConcurrentLinkedQueue<Page> toLoadQueue = CrawlerService.getInstance().getToLoadQueue();
        for (Page p : page.getChildrenPages()) toLoadQueue.offer(p);
    }
}
