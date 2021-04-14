package com.aeloaiei.dissertation.domainexplorer.service;

import com.aeloaiei.dissertation.documenthandler.api.clients.DocumentHandlerClient;
import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import com.aeloaiei.dissertation.domainexplorer.http.HTMLParser;
import com.aeloaiei.dissertation.domainexplorer.http.HTTPResourceRetriever;
import com.aeloaiei.dissertation.domainexplorer.http.RawWebResource;
import com.aeloaiei.dissertation.domainexplorer.http.RobotsPolicy;
import com.aeloaiei.dissertation.domainexplorer.http.RobotsTxtParser;
import com.aeloaiei.dissertation.domainfeeder.api.clients.DomainFeederClient;
import com.aeloaiei.dissertation.domainfeeder.api.dto.DomainDto;
import com.aeloaiei.dissertation.urlfrontier.api.clients.UrlFrontierClient;
import com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus;
import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.aeloaiei.dissertation.domainexplorer.utils.Configuration.RESOURCE_REQUEST_DELAY_IN_MILLISECONDS;
import static com.aeloaiei.dissertation.domainexplorer.utils.Configuration.USER_AGENT;
import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.FAILED;
import static com.aeloaiei.dissertation.urlfrontier.api.dto.CrawlingStatus.NOT_ALLOWED_ROBOTS_TXT;
import static java.time.LocalDateTime.now;

@Service
public class SingleDomainExplorerDaemon implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(SingleDomainExplorerDaemon.class);
    private static final LocalDateTime ONE_HUNDRED_YEARS_AGO = now().minusYears(100);

    @Autowired
    private DomainFeederClient domainFeederClient;
    @Autowired
    private UrlFrontierClient urlFrontierClient;
    @Autowired
    private StorageDaemon storageDaemon;
    @Autowired
    private HTTPResourceRetriever httpResourceRetriever;
    @Autowired
    private HTMLParser htmlParser;
    @Autowired
    private RobotsTxtParser robotsTxtParser;

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
            Thread.sleep(RESOURCE_REQUEST_DELAY_IN_MILLISECONDS);
            Pair<DomainDto, RobotsPolicy> domainAndPolicy = getDomainToCrawl();
            Collection<UniformResourceLocatorDto> urls = urlFrontierClient.getExplorableURLs(domainAndPolicy.getLeft().getName());

            if (urls.isEmpty()) {
                LOGGER.warn("No url found for domain: " + domainAndPolicy.getLeft().getName());
            } else {
                LOGGER.info("Exploring domain: " + domainAndPolicy.getLeft().getName());
                exploreNewBatch(urls, domainAndPolicy.getLeft(), domainAndPolicy.getRight());
            }
        } catch (RuntimeException e) {
            LOGGER.error("Exploring finished with error.. Retrying..", e);
        }
    }

    private Pair<DomainDto, RobotsPolicy> getDomainToCrawl() {
        try {

            DomainDto domainDto = domainFeederClient.getCrawlableDomain();
            RobotsPolicy robotsPolicy = getRobotsPolicy(domainDto);

            return Pair.of(domainDto, robotsPolicy);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to retrieve domain for crawling", e);
        }
    }

    private RobotsPolicy getRobotsPolicy(DomainDto domain) {
        String robotsTxtLocation = getRobotsTxtLocation(domain);
        RobotsPolicy robotsPolicy = new RobotsPolicy(USER_AGENT);

        try {
            UniformResourceLocatorDto robotsTxtURL = new UniformResourceLocatorDto(robotsTxtLocation);
            Optional<RawWebResource> robotsTxtWebResource = httpResourceRetriever.retrieve(robotsTxtURL, USER_AGENT);

            if (!robotsTxtWebResource.isPresent() || robotsTxtWebResource.get().getContent().isEmpty()) {
                LOGGER.warn("Failed to get robots.txt for domain: " + domain.getName());
            } else {
                robotsPolicy = robotsTxtWebResource.map(x -> robotsTxtParser.parse(x, USER_AGENT)).get();
            }
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed robots.txt URL : " + robotsTxtLocation);
        }

        return robotsPolicy;
    }

    private String getRobotsTxtLocation(DomainDto domain) {
        return domain.getName() + "/robots.txt";
    }

    private void exploreNewBatch(Collection<UniformResourceLocatorDto> urls, DomainDto domain, RobotsPolicy robotsPolicy) throws InterruptedException {
        for (UniformResourceLocatorDto url : urls) {
            Thread.sleep(RESOURCE_REQUEST_DELAY_IN_MILLISECONDS);

            if (!isAllowedToExplore(url, robotsPolicy)) {
                LOGGER.debug("Not allowed to explore: " + url.getLocation());
                url.setCrawlingStatus(NOT_ALLOWED_ROBOTS_TXT);
            } else {
                LOGGER.debug("Exploring: " + url.getLocation());
                exploreUrl(url);
            }

            updateCrawledDomain(domain);
            updateCrawledURL(url);
        }
    }

    private void exploreUrl(UniformResourceLocatorDto url) {
        Optional<RawWebResource> rawWebResource = httpResourceRetriever.retrieve(url, USER_AGENT);

        if (!rawWebResource.isPresent()) {
            LOGGER.error("Failed to explore: " + url);
            url.setCrawlingStatus(FAILED);
        } else {
            WebDocumentDto exploredWebDocument = htmlParser.parse(rawWebResource.get(), url);

            putDiscoveredDomains(url.getDomainsReferred());
            putDiscoveredURLs(url.getLinksReferred());
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

    private void putDiscoveredDomains(Set<String> domains) {
        Map<String, DomainDto> domainDtos = new HashMap<>();

        for (String domain : domains) {
            DomainDto domainDto = new DomainDto();

            domainDto.setName(domain);
            domainDto.setLastCrawled(ONE_HUNDRED_YEARS_AGO);
            domainDtos.put(domain, domainDto);
        }

        storageDaemon.putDiscoveredDomains(domainDtos);
    }

    private void putDiscoveredURLs(Set<String> links) {
        Map<String, UniformResourceLocatorDto> urls = new HashMap<>();

        for (String link : links) {
            try {
                UniformResourceLocatorDto url = new UniformResourceLocatorDto(link);

                url.setLastCrawled(ONE_HUNDRED_YEARS_AGO);
                urls.put(url.getLocation(), url);
            } catch (MalformedURLException e) {
                LOGGER.error("Failed to construct URL from link: " + link, e);
            }
        }

        storageDaemon.putDiscoveredURLs(urls);
    }

    private void updateCrawledDomain(DomainDto domain) {
        domain.setLastCrawled(now());
        storageDaemon.putExploredDomain(domain);
    }

    private void updateCrawledURL(UniformResourceLocatorDto url) {
        url.setLastCrawled(now());
        storageDaemon.putExploredURL(url);
    }
}
