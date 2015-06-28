package ru.webcrawler;

import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertEquals;

/**
 * User: pyotruk
 * Date: 2015-06-28
 */

public class LoadPageTaskTests {

    @Test
    public void testRun() {
        Page page = new Page("http://ya.ru/", 0);
        Queue<Page> toLoadQueue = new ConcurrentLinkedQueue<>();
        Queue<Page> toSaveQueue = new ConcurrentLinkedQueue<>();

        LoadPageTask task = new LoadPageTask(page, 1, toLoadQueue, toSaveQueue);
        task.start();

        try {
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("toSaveQueue size must be 1", 1, toSaveQueue.size());
        assertEquals("toSaveQueue must contains start page", page, toSaveQueue.peek());
        assertEquals(
                "toLoadQueue size must be equals children pages number of the start page",
                page.getChildrenPages().size(), toLoadQueue.size());
    }

}
