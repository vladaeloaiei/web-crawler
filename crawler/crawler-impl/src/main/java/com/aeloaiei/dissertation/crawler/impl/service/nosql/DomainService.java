package com.aeloaiei.dissertation.crawler.impl.service.nosql;

import com.aeloaiei.dissertation.crawler.impl.model.nosql.Domain;
import com.aeloaiei.dissertation.crawler.impl.repository.nosql.DomainRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DomainService {
    private static final Logger LOGGER = LogManager.getLogger(DomainService.class);

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Optional<Domain> getCrawlableDomain() {
        Optional<Domain> domain = domainRepository.findTopByOrderByLastCrawledAsc();

        if (domain.isPresent()) {
            Domain currentDomain = domain.get();

            currentDomain.setLastCrawled(LocalDateTime.now());
            LOGGER.info("Sending domain for crawl: " + domain);
            return Optional.of(domainRepository.save(currentDomain));
        } else {
            LOGGER.warn("No domain found");
            return Optional.empty();
        }

    }

    public List<Domain> putAllExplored(List<Domain> domains) {
        return domainRepository.saveAll(domains);
    }

    public List<Domain> putAllNew(List<Domain> domains) {
        for (Domain domain : domains) {
            put(domain);
        }

        return domainRepository.saveAll(domains);
    }

    private void put(Domain domain) {
        domainRepository.findByName(domain.getName())
                .ifPresent(currentDomain ->
                        domain.setLastCrawled(getNewest(domain.getLastCrawled(), currentDomain.getLastCrawled())));

        domainRepository.save(domain);
    }

    private LocalDateTime getNewest(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }
}
