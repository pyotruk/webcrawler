package ru.webcrawler;

import org.junit.Test;

import java.util.ArrayList;

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
        assertEquals("page must have children", false, page.getChildrenPages().isEmpty());
        assertEquals("child's depth must be 1", 1, new ArrayList<>(page.getChildrenPages()).get(0).getDepth());
    }

}
