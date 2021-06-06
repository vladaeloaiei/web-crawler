package com.aeloaiei.dissertation.domainfeeder.impl.service;

import com.aeloaiei.dissertation.domainfeeder.impl.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class DomainFilterService {
    private static final Logger LOGGER = LogManager.getLogger(DomainFilterService.class);

    private Set<String> allowedDomains;

    @Autowired
    public DomainFilterService(Configuration config) {
        try {
            allowedDomains = Files.readAllLines(Paths.get(config.allowedDomainsFilePath)).stream()
                    .filter(domain -> !domain.trim().isEmpty())
                    .collect(toSet());

            if (allowedDomains.isEmpty()) {
                LOGGER.info("The allowed_domains.txt config file is empty. Allowing all domains by default.");
            } else {
                LOGGER.warn("Allowing only the following domains:" + allowedDomains);
            }
        } catch (IOException e) {
            allowedDomains = new HashSet<>();
            LOGGER.warn("The allowed_domains.txt config file does not exist. Allowing all domains by default.");
        }
    }

    public boolean isAllowed(String domain) {
        return allowedDomains.isEmpty() || allowedDomains.contains(domain);
    }
}
