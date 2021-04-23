package com.aeloaiei.dissertation.domainfeeder.impl.service;

import com.aeloaiei.dissertation.domainfeeder.impl.model.Domain;
import com.aeloaiei.dissertation.domainfeeder.impl.repository.DomainRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class DomainFeederService {
    private static final Logger LOGGER = LogManager.getLogger(DomainFeederService.class);

    @Autowired
    private DomainFilterService domainFilterService;
    @Autowired
    private DomainRepository domainRepository;

    public Optional<Domain> getCrawlableDomain() {
        Optional<Domain> domain = domainRepository.findTopByOrderByLastCrawledAsc();

        if (domain.isPresent()) {
            Domain currentDomain = domain.get();

            currentDomain.setLastCrawled(now());

            LOGGER.info("Sending domain for crawl: " + domain.get());
            return Optional.of(domainRepository.save(currentDomain));

        } else {
            LOGGER.warn("No domain found");
        }

        return Optional.empty();
    }

    public List<Domain> putAllExplored(List<Domain> domains) {
        List<Domain> domainsToSave = domains.stream()
                .filter(domain -> domainFilterService.isAllowed(domain.getName()))
                .collect(toList());
        return domainRepository.saveAll(domainsToSave);
    }

    public List<Domain> putAllNew(List<Domain> domains) {
        List<Domain> domainsToSave = domains.stream()
                .filter(domain -> domainFilterService.isAllowed(domain.getName()))
                .filter(domain -> !domainRepository.existsByName(domain.getName()))
                .collect(toList());

        return domainRepository.saveAll(domainsToSave);
    }
}
