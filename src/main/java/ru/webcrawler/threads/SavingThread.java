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

    private void doSave() {
        Queue<Page> queue = CrawlerService.getInstance().getToSaveQueue();
        Page page;
        ArrayList<Page> list = new ArrayList<>();
        while ((page = queue.poll()) != null) list.add(page);

        for (Page p : list) {
            //TODO save to DB
        }
        log.info(list.size() + " pages have been saved.");
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                sleep(5000);
                doSave();

            } catch (InterruptedException e) {
                doSave();
                log.warn(e.getMessage());
                break;
            }
        }
    }
}
