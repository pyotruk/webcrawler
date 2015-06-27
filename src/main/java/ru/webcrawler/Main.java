package ru.webcrawler;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class Main {

    public static void main(String[] args) {
        String url = args[0];
        int depth = Integer.parseInt(args[1]);

        CrawlerService.start(url, depth, 10);
    }

}
