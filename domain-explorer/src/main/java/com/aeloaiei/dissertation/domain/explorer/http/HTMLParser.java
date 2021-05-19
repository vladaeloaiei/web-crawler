package com.aeloaiei.dissertation.domain.explorer.http;

import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.NOT_ALLOWED_META_INFO;
import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.SUCCESS;

@Component
public class HTMLParser {
    private static final Logger LOGGER = LogManager.getLogger(HTMLParser.class);
    private static final String ROBOTS_ALLOWED = "allowed";
    private static final String ROBOTS_NO_FOLLOW = "nofollow";
    private static final String ROBOTS_NO_INDEX = "noindex";
    private static final String ROBOTS_NONE = "none";

    public WebDocumentDto parse(RawWebResource rawWebResource, UniformResourceLocatorDto url) {
        Document doc = Jsoup.parse(rawWebResource.getContent(), rawWebResource.getLocation());
        Elements metaElements = doc.getElementsByTag("meta");
        Elements linkElements = doc.getElementsByTag("a");
        Set<String> links = new HashSet<>();
        Set<String> domains = new HashSet<>();
        String title = doc.title();
        String content = "";

        switch (getPolicy(metaElements)) {
            case ROBOTS_ALLOWED:
                content = getContent(doc);
                links = getLinks(rawWebResource.getLocation(), linkElements);
                domains = getDomains(links);
                url.setCrawlingStatus(SUCCESS);
                break;
            case ROBOTS_NO_FOLLOW:
                content = getContent(doc);
                url.setCrawlingStatus(SUCCESS);
                break;
            case ROBOTS_NO_INDEX:
                links = getLinks(rawWebResource.getLocation(), linkElements);
                url.setCrawlingStatus(NOT_ALLOWED_META_INFO);
                break;
            default: //ROBOTS_NONE
                break;
        }

        url.getLinksReferred().addAll(links);
        url.getDomainsReferred().addAll(domains);

        return new WebDocumentDto(rawWebResource.getLocation(), title, content);
    }

    private String getPolicy(Elements metaElements) {
        for (Element metaElement : metaElements) {
            if (metaElement.attr("name").equals("robots")) {
                if (metaElement.attr("content").toLowerCase().contains(ROBOTS_NO_FOLLOW)) {
                    return ROBOTS_NO_FOLLOW;
                }

                if (metaElement.attr("content").toLowerCase().contains(ROBOTS_NO_INDEX)) {
                    return ROBOTS_NO_INDEX;
                }

                if (metaElement.attr("content").toLowerCase().contains(ROBOTS_NONE)) {
                    return ROBOTS_NONE;
                }
            }
        }

        return ROBOTS_ALLOWED;
    }

    private String getContent(Document document) {
        AppenderNodeVisitor appenderNodeVisitor = new AppenderNodeVisitor();
        NodeTraversor.traverse(appenderNodeVisitor, document);

        return appenderNodeVisitor.reset();
    }

    private Set<String> getLinks(String sourceLink, Elements linkElements) {
        Set<String> links = new HashSet<>();
        String link;

        for (Element linkElement : linkElements) {
            if (0 != linkElement.attr("href").length()) {
                if (linkElement.attr("abs:href").contains("#")) {
                    link = linkElement.attr("abs:href")
                            .substring(0, linkElement.attr("abs:href").indexOf('#') - 1);
                } else {
                    link = linkElement.attr("abs:href");
                }

                if (isValidURL(sourceLink, link)) {
                    links.add(link);
                }
            }
        }

        return links;
    }

    private boolean isValidURL(String sourceLink, String link) {
        try {
            if (Objects.equals(sourceLink, link)) {
                return false;
            } else {
                new URL(link);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private Set<String> getDomains(Set<String> links) {
        Set<String> domains = new HashSet<>();

        for (String link : links) {
            try {
                UniformResourceLocatorDto url = new UniformResourceLocatorDto(link);

                domains.add(url.getDomain());
            } catch (Exception e) {
                LOGGER.error("Failed to create URL from link: " + link, e);
            }
        }

        return domains;
    }
}
