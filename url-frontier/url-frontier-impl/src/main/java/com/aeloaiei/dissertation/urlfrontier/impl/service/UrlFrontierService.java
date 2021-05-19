package com.aeloaiei.dissertation.urlfrontier.impl.service;

import com.aeloaiei.dissertation.domainfeeder.api.clients.DomainFeederClient;
import com.aeloaiei.dissertation.domainfeeder.api.dto.DomainDto;
import com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration;
import com.aeloaiei.dissertation.urlfrontier.impl.model.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.repository.UniformResourceLocatorRepository;
import com.mongodb.MongoCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration.RETRY_MAX_ATTEMPTS;
import static com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration.RETRY_MAX_DELAY;
import static com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration.RETRY_MIN_DELAY;
import static java.lang.Math.ceil;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class UrlFrontierService {
    private static final Logger LOGGER = LogManager.getLogger(UrlFrontierService.class);
    private static final Supplier<LocalDateTime> THIRTY_DAYS_AGO = () -> now().minusDays(30);
    private static final Supplier<LocalDateTime> ONE_HUNDRED_YEARS_AGO = () -> now().minusYears(100);

    @Autowired
    private Configuration config;
    @Autowired
    private DomainFeederClient domainFeederClient;
    @Autowired
    private UniformResourceLocatorRepository uniformResourceLocatorRepository;

    public void put(Collection<UniformResourceLocator> urls) {
        Map<String, Set<UniformResourceLocator>> domainsDiscoveredUrlsMap = extractDiscoveredSplitByDomains(urls);
        Set<String> savedDomains = putDiscoveredDomains(domainsDiscoveredUrlsMap.keySet());
        Set<UniformResourceLocator> filteredDiscoveredUrls = filterUrls(domainsDiscoveredUrlsMap, savedDomains);

        saveAllExplored(urls);
        saveAllNew(filteredDiscoveredUrls);
    }

    private Map<String, Set<UniformResourceLocator>> extractDiscoveredSplitByDomains(Collection<UniformResourceLocator> urls) {
        Map<String, Set<UniformResourceLocator>> domainsDiscoveredUrlsMap = new HashMap<>();

        for (UniformResourceLocator url : urls) {
            if (!domainsDiscoveredUrlsMap.containsKey(url.getDomain())) {
                domainsDiscoveredUrlsMap.put(url.getDomain(), new HashSet<>());
            }

            for (String discoveredLink : url.getLinksReferred()) {
                Optional<UniformResourceLocator> discoveredUrl = buildDiscoveredUrl(discoveredLink);

                if (discoveredUrl.isPresent()) {
                    if (!domainsDiscoveredUrlsMap.containsKey(discoveredUrl.get().getDomain())) {
                        domainsDiscoveredUrlsMap.put(discoveredUrl.get().getDomain(), new HashSet<>());
                    }

                    domainsDiscoveredUrlsMap.get(discoveredUrl.get().getDomain()).add(discoveredUrl.get());
                }
            }
        }

        return domainsDiscoveredUrlsMap;
    }

    private Optional<UniformResourceLocator> buildDiscoveredUrl(String link) {
        try {
            UniformResourceLocator url = new UniformResourceLocator(link);

            url.setLastCrawled(THIRTY_DAYS_AGO.get());
            return Optional.of(url);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to build url from link = " + link);
        }

        return Optional.empty();
    }

    private Set<String> putDiscoveredDomains(Set<String> domains) {
        return domainFeederClient.put(buildDiscoveredDomains(domains))
                .stream()
                .map(DomainDto::getName)
                .collect(toSet());
    }

    private Set<DomainDto> buildDiscoveredDomains(Collection<String> domains) {
        return domains.stream()
                .map(domain -> new DomainDto(domain, ONE_HUNDRED_YEARS_AGO.get()))
                .collect(toSet());
    }

    private Set<UniformResourceLocator> filterUrls(Map<String, Set<UniformResourceLocator>> domainsDiscoveredUrlsMap, Set<String> domains) {
        Set<UniformResourceLocator> filteredUrls = new HashSet<>();

        for (String domain : domains) {
            if (domainsDiscoveredUrlsMap.containsKey(domain)) {
                filteredUrls.addAll(domainsDiscoveredUrlsMap.get(domain));
            }
        }

        return filteredUrls;
    }

    @Transactional
    @Retryable(value = {MongoCommandException.class, UncategorizedMongoDbException.class},
            listeners = {"retryListenerSupportLogger"},
            backoff = @Backoff(delay = RETRY_MIN_DELAY, maxDelay = RETRY_MAX_DELAY),
            maxAttempts = RETRY_MAX_ATTEMPTS)
    public void saveAllExplored(Collection<UniformResourceLocator> urls) {
        LOGGER.info("Updating " + urls.size() + " explored urls");
        uniformResourceLocatorRepository.saveAll(urls);
    }

    @Transactional
    @Retryable(value = {MongoCommandException.class, UncategorizedMongoDbException.class},
            listeners = {"retryListenerSupportLogger"},
            backoff = @Backoff(delay = RETRY_MIN_DELAY, maxDelay = RETRY_MAX_DELAY),
            maxAttempts = RETRY_MAX_ATTEMPTS)
    public void saveAllNew(Collection<UniformResourceLocator> urls) {
        Collection<UniformResourceLocator> urlsToSave = urls.stream()
                .filter(url -> !uniformResourceLocatorRepository.existsByLocation(url.getLocation()))
                .collect(toList());

        LOGGER.info("Saving " + urlsToSave.size() + " new urls");
        uniformResourceLocatorRepository.saveAll(urlsToSave);
    }

    @Transactional
    @Retryable(value = {MongoCommandException.class, UncategorizedMongoDbException.class},
            listeners = {"retryListenerSupportLogger"},
            backoff = @Backoff(delay = RETRY_MIN_DELAY, maxDelay = RETRY_MAX_DELAY),
            maxAttempts = RETRY_MAX_ATTEMPTS)
    public List<UniformResourceLocator> getExplorableURLs() {
        DomainDto domain = domainFeederClient.getCrawlableDomain();
        int count = (int) ceil(config.crawlPercentage * getNumberOfResourcesForDomain(domain.getName()));
        List<UniformResourceLocator> urls = getForDomain(domain.getName(), count);

        updateCrawlTimeForUrls(urls);
        LOGGER.info("Sending for crawling " + urls.size() + " urls");
        return urls;
    }

    private int getNumberOfResourcesForDomain(String domain) {
        return uniformResourceLocatorRepository.countByDomain(domain);
    }

    private List<UniformResourceLocator> getForDomain(String domain, int count) {
        Pageable pageable = PageRequest.of(0, count);

        return uniformResourceLocatorRepository.findByDomainOrderByLastCrawledAsc(domain, pageable);
    }

    private void updateCrawlTimeForUrls(List<UniformResourceLocator> urls) {
        urls.forEach(url -> url.setLastCrawled(now()));
        uniformResourceLocatorRepository.saveAll(urls);
    }
}
