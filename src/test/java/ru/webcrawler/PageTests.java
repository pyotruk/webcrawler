package ru.webcrawler;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testGetHost() {
        Page page = new Page("http://ya.ru/", 0);
        assertEquals("", "ya.ru", page.getHost());
    }

    @Test
    public void testNormalizeAndValidateURL() {
        String url = Page.normalizeAndValidateURL("http://ya.ru/", "ololo");
        assertEquals("", "http://ya.ru/", url);

        url = Page.normalizeAndValidateURL("//ya.ru/", "ololo");
        assertEquals("", "http://ya.ru/", url);

        url = Page.normalizeAndValidateURL("/hello", "ya.ru");
        assertEquals("", "http://ya.ru/hello", url);
    }

}
