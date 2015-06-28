package ru.webcrawler.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.Page;

import java.util.Queue;


/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class LoadPageTask extends Thread {

    private static final Logger log = LoggerFactory.getLogger(LoadPageTask.class);

    private Page page;
    private int maxDepth;
    private Queue<Page> toLoadQueue;
    private Queue<Page> toSaveQueue;

    public LoadPageTask(Page page, int maxDepth, Queue<Page> toLoadQueue, Queue<Page> toSaveQueue) {
        this.page = page;
        this.maxDepth = maxDepth;
        this.toLoadQueue = toLoadQueue;
        this.toSaveQueue = toSaveQueue;
    }

    @Override
    public void run() {
        if (page.getDepth() > maxDepth) {
            log.debug("MaxDepth ({}) has reached.", maxDepth);
            return;
        }
        page.load();
        toSaveQueue.offer(page);

        for (Page p : page.getChildrenPages()) toLoadQueue.offer(p);
    }
}
