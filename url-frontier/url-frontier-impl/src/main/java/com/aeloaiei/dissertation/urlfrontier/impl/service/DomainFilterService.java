package com.aeloaiei.dissertation.urlfrontier.impl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

@Service
public class DomainFilterService {
    private Set<String> allowedDomains;

    @Autowired
    public DomainFilterService(ResourceLoader resourceLoader) {
        try {
            Resource resource = resourceLoader.getResource("classpath:allowed.txt");

            allowedDomains = new HashSet<>(Files.readAllLines(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the allowed.txt config file", e);
        }
    }

    public boolean isAllowed(String domain) {
        return allowedDomains.contains(domain);
    }
}
