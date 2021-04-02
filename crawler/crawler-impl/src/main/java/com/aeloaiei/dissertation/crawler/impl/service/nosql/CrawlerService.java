package com.aeloaiei.dissertation.crawler.impl.service.nosql;

import com.aeloaiei.dissertation.crawler.impl.model.nosql.Domain;
import com.aeloaiei.dissertation.crawler.impl.repository.nosql.DomainRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
public class CrawlerService {
    private static final Logger LOGGER = LogManager.getLogger(CrawlerService.class);
    private static final int MINIM_SECONDS_FROM_LAST_CRAWL = 30;

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Optional<Domain> getCrawlableDomain() {
        Optional<Domain> domain = domainRepository.findTopByOrderByLastCrawledAsc();

        if (domain.isPresent()) {
            Domain currentDomain = domain.get();

            if (isValidForCrawling(currentDomain)) {
                currentDomain.setLastCrawled(now());

                LOGGER.info("Sending domain for crawl: " + domain);
                return Optional.of(domainRepository.save(currentDomain));
            } else {
                LOGGER.warn("No domain available for crawling found");
            }
        } else {
            LOGGER.warn("No domain found");
        }

        return Optional.empty();
    }

    private boolean isValidForCrawling(Domain domain) {
        return domain.getLastCrawled().isBefore(now().minusSeconds(MINIM_SECONDS_FROM_LAST_CRAWL));
    }

    public List<Domain> putAllExplored(List<Domain> domains) {
        return domainRepository.saveAll(domains);
    }

    public List<Domain> putAllNew(List<Domain> domains) {
        for (Domain domain : domains) {
            if (!domainRepository.existsByName(domain.getName())) {
                domainRepository.save(domain);
            }
        }

        return domains;
    }
}
