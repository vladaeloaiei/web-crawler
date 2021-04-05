package com.aeloaiei.dissertation.domainexplorer.http;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.WebDocument;
import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class HTMLParser {
    private static final Logger LOGGER = LogManager.getLogger(HTMLParser.class);
    private static final String ROBOTS_ALLOWED = "allowed";
    private static final String ROBOTS_NO_FOLLOW = "nofollow";
    private static final String ROBOTS_NO_INDEX = "noindex";
    private static final String ROBOTS_NONE = "none";

    public Pair<WebDocument, UniformResourceLocatorDto> parse(RawWebResource rawWebResource, UniformResourceLocatorDto url) {
        Document doc = Jsoup.parse(rawWebResource.getContent(), rawWebResource.getLocation());
        Elements metaElements = doc.getElementsByTag("meta");
        Elements linkElements = doc.getElementsByTag("a");
        WebDocument webDocument;
        Set<String> links = new HashSet<>();
        Set<String> domains = new HashSet<>();

        String content = "";

        switch (getPolicy(metaElements)) {
            case ROBOTS_ALLOWED:
                content = getContent(doc);
                links = getLinks(rawWebResource.getLocation(), linkElements);
                domains = getDomains(links);
                break;
            case ROBOTS_NO_FOLLOW:
                content = getContent(doc);
                break;
            case ROBOTS_NO_INDEX:
                links = getLinks(rawWebResource.getLocation(), linkElements);
                domains = getDomains(links);
                break;
            default: //ROBOTS_NONE
                break;
        }

        url.getLinksReferred().addAll(links);
        url.getDomainsReferred().addAll(domains);
        webDocument = new WebDocument(rawWebResource.getLocation(), content, rawWebResource.getStatus().value());

        return Pair.of(webDocument, url);
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
        return document.text();
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