package com.aeloaiei.dissertation.urlfrontier.impl.service;

import com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Service
public class DomainFilterService {
    private Set<String> allowedDomains;

    @Autowired
    public DomainFilterService(Configuration config) {
        try {
            allowedDomains = new HashSet<>(Files.readAllLines(Paths.get(config.allowedDomainsFilePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the allowed_domains.txt config file", e);
        }
    }

    public boolean isAllowed(String domain) {
        return allowedDomains.isEmpty() || allowedDomains.contains(domain);
    }
}
