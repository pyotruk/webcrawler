package ru.webcrawler;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
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

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "depth")
    private int depth;

    @Column(name = "http_code")
    private int httpCode = 0;

    @Lob
    @Column(name = "content")
    private String content;

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
                    String rawUrl = a.attr("href");
                    String url = normalizeAndValidateURL(rawUrl, getHost());
                    if (url != null) {
                        boolean ok = childrenPages.add(new Page(url, depth + 1));
                        if (!ok) log.info("Page is duplicated, skipped. [url:{}]", url);
                    } else {
                        log.debug("URL is not valid, skipped. [url:{}]", rawUrl);
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

    public static String normalizeAndValidateURL(String url, String host) {
        UrlValidator validator = new UrlValidator();
        boolean isValid = validator.isValid(url);

        if (!isValid) {
            if (url.startsWith("//")) { // URLs like //yandex.ru
                url = "http:" + url;
                isValid = validator.isValid(url);

            } else { // relative URLs
                url = "http://" + host + url;
                isValid = validator.isValid(url);
            }
        }

        if (isValid) return url;
        else return null;
    }

    public String getHost() {
        try {
            return (new URL(url)).getHost();

        } catch (MalformedURLException e) {
            log.debug("getHost error: {}", e.getMessage());
            return url;
        }
    }

}
