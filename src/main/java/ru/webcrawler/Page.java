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
    private int httpCode = 0;
    private String content;
    private List<Page> childrenPages = new ArrayList<>();

    private final static int TIMEOUT = 5000;

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
        return "Page[depth:" + depth + "][httpCode:" + httpCode + "][url:" + url + "][children:" + childrenPages.size() + "]";
    }

    public void load() {
        try {
            Connection.Response response = Jsoup.connect(url).timeout(TIMEOUT).execute();
            httpCode = response.statusCode();

            try {
                Document doc = response.parse();
                content = doc.text();

                //TODO add filtering: relative URLs, similar URLs, href=#
                //TODO add relative URLs processing

                for (Element a : doc.select("a")) {
                    childrenPages.add(new Page(a.attr("href"), depth + 1));
                }

            } catch (Exception e) {
                log.warn("Page parsing error: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.warn("Page loading error: {}", e.getMessage());
        }

        log.info(this.toString());
    }

}
