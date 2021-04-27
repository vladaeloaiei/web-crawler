package com.aeloaiei.dissertation.urlfrontier.impl.service;

import com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration;
import com.aeloaiei.dissertation.urlfrontier.impl.model.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.repository.UniformResourceLocatorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static java.lang.Math.ceil;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class UrlFrontierService {
    private static final Logger LOGGER = LogManager.getLogger(UrlFrontierService.class);

    @Autowired
    private Configuration config;
    @Autowired
    private DomainFilterService domainFilterService;
    @Autowired
    private UniformResourceLocatorRepository uniformResourceLocatorRepository;

    public void putAllExplored(Collection<UniformResourceLocator> urls) {
        List<UniformResourceLocator> urlsToSave = urls.stream()
                .filter(url -> domainFilterService.isAllowed(url.getDomain()))
                .collect(toList());

        LOGGER.info("Updating " + urlsToSave.size() + " explored urls");
        uniformResourceLocatorRepository.saveAll(urlsToSave);
    }

    public void putAllNew(Collection<UniformResourceLocator> urls) {
        List<UniformResourceLocator> urlsToSave = urls.stream()
                .filter(url -> domainFilterService.isAllowed(url.getDomain()))
                .filter(url -> !uniformResourceLocatorRepository.existsByLocation(url.getLocation()))
                .collect(toList());

        LOGGER.info("Saving " + urlsToSave.size() + " new urls");
        uniformResourceLocatorRepository.saveAll(urlsToSave);
    }

    public List<UniformResourceLocator> getExplorableURLs(String domain) {
        int count = (int) ceil(config.crawlPercentage * getNumberOfResourcesForDomain(domain));
        List<UniformResourceLocator> urls = getForDomain(domain, count);

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
