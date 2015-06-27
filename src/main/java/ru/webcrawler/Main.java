package ru.webcrawler;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public class Main {

    public static void main(String[] args) {
        String url = args[0];
        int deep = Integer.parseInt(args[1]);

        Page page = new Page(url, 0);
        page.load();
        System.out.println(page);
    }

}
