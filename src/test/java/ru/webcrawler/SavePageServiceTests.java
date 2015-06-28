package ru.webcrawler;

import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;


/**
 * User: pyotruk
 * Date: 2015-06-28
 */

public class SavePageServiceTests {

    @Test
    public void testRun() {
        Page page = new Page("http://ya.ru/", 0);
        Queue<Page> toSaveQueue = new ConcurrentLinkedQueue<>();
        toSaveQueue.offer(page);

        SavePageService service = SavePageService.createInstance(toSaveQueue);
        service.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        service.interrupt();

        try {
            service.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("toSaveQueue must be empty", true, toSaveQueue.isEmpty());
    }

}
