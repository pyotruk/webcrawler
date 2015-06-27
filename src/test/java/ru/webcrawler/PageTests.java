package ru.webcrawler;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class PageTests {

    @Test
    public void testLoad() {
        Page page = new Page("http://ya.ru/", 0);
        page.load();

        assertEquals("httpCode must be 200", 200, page.getHttpCode());
        assertThat("page must have children", page.getChildrenPages().size(), not(0));
        assertEquals("child's depth must be 1", 1, page.getChildrenPages().get(0).getDepth());
    }

}
