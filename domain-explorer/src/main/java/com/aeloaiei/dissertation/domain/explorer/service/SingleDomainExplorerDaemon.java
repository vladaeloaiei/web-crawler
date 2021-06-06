package com.aeloaiei.dissertation.domain.explorer.service;

import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import com.aeloaiei.dissertation.domain.explorer.config.Configuration;
import com.aeloaiei.dissertation.domain.explorer.http.HTMLParser;
import com.aeloaiei.dissertation.domain.explorer.http.HTTPResourceRetriever;
import com.aeloaiei.dissertation.domain.explorer.http.RawWebResource;
import com.aeloaiei.dissertation.domain.explorer.http.RobotsPolicy;
import com.aeloaiei.dissertation.domain.explorer.http.RobotsTxtParser;
import com.aeloaiei.dissertation.urlfrontier.api.clients.UrlFrontierClient;
import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.FAILED;
import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.NOT_ALLOWED_ROBOTS_TXT;
import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.NOT_TEXT_RESOURCE;
import static java.time.LocalDateTime.now;

@Service
public class SingleDomainExplorerDaemon implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(SingleDomainExplorerDaemon.class);

    private Configuration config;
    private UrlFrontierClient urlFrontierClient;
    private StorageDaemon storageDaemon;
    private HTTPResourceRetriever httpResourceRetriever;
    private HTMLParser htmlParser;
    private RobotsTxtParser robotsTxtParser;

    @Autowired
    public SingleDomainExplorerDaemon(Configuration config,
                                      UrlFrontierClient urlFrontierClient,
                                      StorageDaemon storageDaemon,
                                      HTTPResourceRetriever httpResourceRetriever,
                                      HTMLParser htmlParser,
                                      RobotsTxtParser robotsTxtParser) {
        this.config = config;
        this.urlFrontierClient = urlFrontierClient;
        this.storageDaemon = storageDaemon;
        this.httpResourceRetriever = httpResourceRetriever;
        this.htmlParser = htmlParser;
        this.robotsTxtParser = robotsTxtParser;
    }

    @Override
    public void run() {
        try {
            while (true) {
                explore();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Explorer exited. Error: ", e);
        }
    }

    private void explore() throws InterruptedException {
        try {
            Thread.sleep(getRandomDelay());
            List<UniformResourceLocatorDto> urls = getUrlsToCrawl();

            if (urls.isEmpty()) {
                LOGGER.warn("No url received.. Retrying.. ");
            } else {
                String domain = urls.get(0).getDomain();
                RobotsPolicy robotsPolicy = getRobotsPolicy(domain);

                LOGGER.info("Exploring domain: " + domain);
                exploreNewBatch(urls, robotsPolicy);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Exploring finished with error.. Retrying..", e);
        }
    }

    private long getRandomDelay() {
        return (long) (1000 * Math.random()) + config.resourceRequestDelay;
    }

    private RobotsPolicy getRobotsPolicy(String domain) {
        String robotsTxtLocation = getRobotsTxtLocation(domain);
        RobotsPolicy robotsPolicy = new RobotsPolicy(config.userAgent);

        try {
            UniformResourceLocatorDto robotsTxtURL = new UniformResourceLocatorDto(robotsTxtLocation);
            Optional<RawWebResource> robotsTxtWebResource = httpResourceRetriever.retrieve(robotsTxtURL, config.userAgent);

            if (!robotsTxtWebResource.isPresent() || robotsTxtWebResource.get().getContent().isEmpty()) {
                LOGGER.warn("Failed to get robots.txt for domain: " + domain);
            } else {
                robotsPolicy = robotsTxtWebResource.map(x -> robotsTxtParser.parse(x, config.userAgent)).get();
            }
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed robots.txt URL : " + robotsTxtLocation);
        }

        return robotsPolicy;
    }

    private String getRobotsTxtLocation(String domain) {
        return domain + "/robots.txt";
    }

    private List<UniformResourceLocatorDto> getUrlsToCrawl() {
        return urlFrontierClient.getExplorableURLs();
    }

    private void exploreNewBatch(Collection<UniformResourceLocatorDto> urls, RobotsPolicy robotsPolicy) throws InterruptedException {
        for (UniformResourceLocatorDto url : urls) {
            Thread.sleep(getRandomDelay());

            if (!isAllowedToExplore(url, robotsPolicy)) {
                LOGGER.debug("Not allowed to explore: " + url.getLocation());
                url.setCrawlingStatus(NOT_ALLOWED_ROBOTS_TXT);
            } else {
                LOGGER.debug("Exploring: " + url.getLocation());
                exploreUrl(url);
            }

            updateCrawledURL(url);
        }
    }

    private void exploreUrl(UniformResourceLocatorDto url) {
        Optional<RawWebResource> rawWebResource = httpResourceRetriever.retrieve(url, config.userAgent);

        if (!rawWebResource.isPresent()) {
            LOGGER.error("Failed to explore: " + url);
            url.setCrawlingStatus(FAILED);
        } else if (!rawWebResource.get().isText()) {
            LOGGER.warn("The resource is not text type: " + url);
            url.setCrawlingStatus(NOT_TEXT_RESOURCE);
        } else {
            WebDocumentDto exploredWebDocument = htmlParser.parse(rawWebResource.get(), url);

            storageDaemon.putExploredDocument(exploredWebDocument);
        }
    }

    public boolean isAllowedToExplore(UniformResourceLocatorDto url, RobotsPolicy robotsPolicy) {
        for (String allow : robotsPolicy.getAllow()) {
            if (url.getPath().startsWith(allow)) {
                return true;
            }
        }

        for (String disallow : robotsPolicy.getDisallow()) {
            if (url.getPath().startsWith(disallow)) {
                return false;
            }
        }

        return true;
    }

    private void updateCrawledURL(UniformResourceLocatorDto url) {
        url.setLastCrawled(now());
        storageDaemon.putExploredURL(url);
    }
}
