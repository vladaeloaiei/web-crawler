package com.aeloaiei.dissertation.urlfrontier.impl.service;

import com.aeloaiei.dissertation.urlfrontier.impl.model.nosql.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.repository.nosql.UniformResourceLocatorRepository;
import com.aeloaiei.dissertation.urlfrontier.impl.utils.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static java.lang.Math.ceil;

@Service
public class UrlFrontierService {
    private static final Logger LOGGER = LogManager.getLogger(UrlFrontierService.class);

    @Autowired
    private UniformResourceLocatorRepository uniformResourceLocatorRepository;

    public void putAllExplored(Collection<UniformResourceLocator> urls) {
        LOGGER.info("Updating explored urls: " + urls);
        uniformResourceLocatorRepository.saveAll(urls);
    }

    public void putAllNew(Collection<UniformResourceLocator> urls) {
        for (UniformResourceLocator url : urls) {
            if (!uniformResourceLocatorRepository.existsByLocation(url.getLocation())) {
                LOGGER.info("Saving new url: " + url);
                uniformResourceLocatorRepository.save(url);
            }
        }
    }

    public List<UniformResourceLocator> getExplorableURLs(String domain) {
        int count = (int) ceil(Configuration.PERCENTAGE_OF_RESOURCES_TO_CRAWL * getNumberOfResourcesForDomain(domain));
        List<UniformResourceLocator> urls = getForDomain(domain, count);

        LOGGER.info("Sending for crawling " + urls.size() + " urls for domain: " + domain);
        return urls;
    }

    private List<UniformResourceLocator> getForDomain(String domain, int count) {
        Pageable pageable = PageRequest.of(0, count);

        return uniformResourceLocatorRepository.findByDomainOrderByPathDesc(domain, pageable);
    }

    private int getNumberOfResourcesForDomain(String domain) {
        return uniformResourceLocatorRepository.countByDomain(domain);
    }
}
