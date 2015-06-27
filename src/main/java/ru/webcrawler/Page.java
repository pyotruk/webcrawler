package ru.webcrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

public final class Page {

    private static final Logger log = LoggerFactory.getLogger(Page.class);

    private String url;
    private int depth;
    private int httpCode;
    private String content;
    private List<Page> childrenPages = new ArrayList<>();

    public Page(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public List<Page> getChildrenPages() {
        return new ArrayList<>(childrenPages);
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        /*StringBuilder sb = new StringBuilder("");
        for (Page page : childrenPages) {
            sb.append("\n\t").append(page);
        }*/
        return "Page[depth:" + depth + "][httpCode:" + httpCode + "][url:" + url + "][children:" + childrenPages.size() + "]";
    }

    public void load() {
        try {
            Connection.Response response = Jsoup.connect(url).execute(); //TODO add timeout
            httpCode = response.statusCode();
            Document doc = response.parse();
            content = doc.text();
            for (Element a : doc.select("a")) {
                childrenPages.add(new Page(a.attr("href"), depth + 1));
            }

        } catch (Exception e) {
            log.error("Page loading error", e);
        }

        log.info(this.toString());
    }

}
