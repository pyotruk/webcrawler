package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;


/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class LoadPageTask extends Thread {

    private static final Logger log = LoggerFactory.getLogger(LoadPageTask.class);

    private final Page page;
    private final int maxDepth;
    private final Queue<Page> toLoadQueue;
    private final Queue<Page> toSaveQueue;

    public LoadPageTask(Page page, int maxDepth, Queue<Page> toLoadQueue, Queue<Page> toSaveQueue) {
        this.page = page;
        this.maxDepth = maxDepth;
        this.toLoadQueue = toLoadQueue;
        this.toSaveQueue = toSaveQueue;
    }

    @Override
    public void run() {
        page.load();
        toSaveQueue.offer(page);

        if (page.getDepth() >= maxDepth) {
            log.debug("MaxDepth ({}) has reached.", maxDepth);
            return;
        }

        for (Page p : page.getChildrenPages()) toLoadQueue.offer(p);
    }
}
