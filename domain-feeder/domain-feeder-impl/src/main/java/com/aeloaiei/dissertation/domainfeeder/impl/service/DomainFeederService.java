package com.aeloaiei.dissertation.domainfeeder.impl.service;

import com.aeloaiei.dissertation.domainfeeder.impl.model.Domain;
import com.aeloaiei.dissertation.domainfeeder.impl.repository.DomainRepository;
import com.mongodb.MongoCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.aeloaiei.dissertation.domainfeeder.impl.config.Configuration.RETRY_MAX_ATTEMPTS;
import static com.aeloaiei.dissertation.domainfeeder.impl.config.Configuration.RETRY_MAX_DELAY;
import static com.aeloaiei.dissertation.domainfeeder.impl.config.Configuration.RETRY_MIN_DELAY;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class DomainFeederService {
    private static final Logger LOGGER = LogManager.getLogger(DomainFeederService.class);

    @Autowired
    private DomainFilterService domainFilterService;
    @Autowired
    private DomainRepository domainRepository;

    @Transactional
    @Retryable(value = {MongoCommandException.class, UncategorizedMongoDbException.class},
            listeners = {"retryListenerSupportLogger"},
            backoff = @Backoff(delay = RETRY_MIN_DELAY, maxDelay = RETRY_MAX_DELAY),
            maxAttempts = RETRY_MAX_ATTEMPTS)
    public Optional<Domain> getCrawlableDomain() {
        long count = domainRepository.count();

        for (int i = 0; i < count; ++i) {
            Optional<Domain> domain = domainRepository.findTopByOrderByLastCrawledAsc();

            domain.ifPresent(d -> {
                d.setLastCrawled(now());
                domainRepository.save(d);
            });

            if (domain.filter(d -> domainFilterService.isAllowed(d.getName())).isPresent()) {
                LOGGER.info("Sending to explore domain: " + domain);
                return domain;
            }
        }

        LOGGER.error("No valid domain found");
        return Optional.empty();
    }

    @Transactional
    @Retryable(value = {MongoCommandException.class, UncategorizedMongoDbException.class},
            listeners = {"retryListenerSupportLogger"},
            backoff = @Backoff(delay = RETRY_MIN_DELAY, maxDelay = RETRY_MAX_DELAY),
            maxAttempts = RETRY_MAX_ATTEMPTS)
    public List<Domain> putAllNew(List<Domain> domains) {
        List<Domain> allowedDomains = domains.stream()
                .filter(domain -> domainFilterService.isAllowed(domain.getName()))
                .collect(toList());
        List<Domain> domainsToSave = allowedDomains.stream()
                .filter(domain -> !domainRepository.existsByName(domain.getName()))
                .collect(toList());

        LOGGER.info("Saving new domains: " + domainsToSave);
        domainRepository.saveAll(domainsToSave);
        return allowedDomains;
    }
}
