package ru.webcrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * User: pyotruk
 * Date: 2015-06-27
 */

@Entity
@Table(name = "PAGE")
public final class Page implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Page.class);

    private static final long serialVersionUID = -8842671428838342400L;

    private final static int TIMEOUT = 5000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;

    @Version
    @Column(name = "version")
    private int version = 0;

    //TODO make unique
    @Column(name = "url")
    private String url;

    @Column(name = "depth")
    private int depth;

    @Column(name = "http_code")
    private int httpCode = 0;

    @Lob
    @Column(name = "content")
    private String content;

    //TODO add parent_id
    @Transient
    private Set<Page> childrenPages = new HashSet<>();

    public Page(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (!url.equals(page.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setVersion(final int version) {
        this.version = version;
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

    public Set<Page> getChildrenPages() {
        return new HashSet<>(childrenPages);
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

                for (Element a : doc.select("a")) {
                    String url = a.attr("href");
                    if(isValidURL(url)) {
                        boolean ok = childrenPages.add(new Page(url, depth + 1));
                        if (!ok) log.info("Page [{}] already exists, skipped.", url);
                    } else {
                        log.info("URL [{}] is not valid, skipped.", url);
                    }
                }

            } catch (Exception e) {
                log.warn("Page parsing error: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.warn("Page loading error: {}", e.getMessage());
        }

        log.info(this.toString());
    }

    private boolean isValidURL(String url) {
        //TODO relative URLs
        //TODO URLs like //yandex.ru
        //TODO regexp
        return url.startsWith("http");
    }

}
