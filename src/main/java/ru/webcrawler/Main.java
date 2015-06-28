package ru.webcrawler;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class Main {

    private final static int DEFAULT_POOL_SIZE = 10;

    public static void main(String[] args) {
        String url = args[0];
        int depth = Integer.parseInt(args[1]);

        int poolSize = DEFAULT_POOL_SIZE;
        if (args.length > 2) poolSize = Integer.parseInt(args[2]);

        CrawlerService service = CrawlerService.createInstance(url, depth, poolSize);
        service.start();
    }

}
