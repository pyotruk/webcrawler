package ru.webcrawler;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * User: pyotruk
 * Date: 2015-06-28
 */

public class CrawlerServiceTests {

    @Test
    public void testRun() {
        CrawlerService service = CrawlerService.createInstance("http://ya.ru/", 1, 10);
        service.start();

        try {
            service.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("toSaveQueue must be empty", 0, service.getToSaveQueueSize());
        assertEquals("toLoadQueue must be empty", 0, service.getToLoadQueueSize());
    }

}
