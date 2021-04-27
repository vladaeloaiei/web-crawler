package com.aeloaiei.dissertation.domainexplorer.service;

import com.aeloaiei.dissertation.documenthandler.api.clients.DocumentHandlerClient;
import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import com.aeloaiei.dissertation.domainexplorer.config.Configuration;
import com.aeloaiei.dissertation.domainfeeder.api.clients.DomainFeederClient;
import com.aeloaiei.dissertation.domainfeeder.api.dto.DomainDto;
import com.aeloaiei.dissertation.urlfrontier.api.clients.UrlFrontierClient;
import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StorageDaemon implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(StorageDaemon.class);

    @Autowired
    private Configuration config;
    @Autowired
    private DomainFeederClient domainFeederClient;
    @Autowired
    private UrlFrontierClient urlFrontierClient;
    @Autowired
    private DocumentHandlerClient documentHandlerClient;

    private Map<String, DomainDto> discoveredDomains = new ConcurrentHashMap<>();
    private Map<String, UniformResourceLocatorDto> discoveredURLs = new ConcurrentHashMap<>();
    private Map<String, DomainDto> exploredDomains = new ConcurrentHashMap<>();
    private Map<String, UniformResourceLocatorDto> exploredURLs = new ConcurrentHashMap<>();
    private Set<WebDocumentDto> exploredDocuments = ConcurrentHashMap.newKeySet();

    public void putDiscoveredDomains(Map<String, DomainDto> domains) {
        discoveredDomains.putAll(domains);
    }

    public void putDiscoveredURLs(Map<String, UniformResourceLocatorDto> urls) {
        discoveredURLs.putAll(urls);
    }

    public void putExploredDomain(DomainDto domain) {
        exploredDomains.put(domain.getName(), domain);
    }

    public void putExploredURL(UniformResourceLocatorDto url) {
        exploredURLs.put(url.getLocation(), url);
    }

    public void putExploredDocument(WebDocumentDto document) {
        exploredDocuments.add(document);
    }

    @Override
    public void run() {
        try {
            while (true) {
                publish();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Failed to publish. Error: ", e);
        }
    }

    private void publish() throws InterruptedException {
        try {
            Thread.sleep(config.metadataUpdateWindow);
            publishDomains();
            publishURLs();
            publishDocuments();
        } catch (RuntimeException e) {
            LOGGER.error("Failed to publish. Error: ", e);
        }
    }

    private void publishDomains() {
        Map<String, DomainDto> tempDiscoveredDomains = discoveredDomains;
        Map<String, DomainDto> tempExploredDomains = exploredDomains;

        if (!discoveredDomains.isEmpty()) {
            discoveredDomains = new ConcurrentHashMap<>();
            LOGGER.debug("Publishing " + tempDiscoveredDomains.values().size() + " discovered domains");
            domainFeederClient.putNewDomains(tempDiscoveredDomains.values());
        }

        if (!exploredDomains.isEmpty()) {
            exploredDomains = new ConcurrentHashMap<>();
            LOGGER.debug("Publishing " + tempExploredDomains.values() + " explored domains");
            domainFeederClient.putExploredDomains(tempExploredDomains.values());
        }
    }

    private void publishURLs() {
        Map<String, UniformResourceLocatorDto> tempDiscoveredURLs = discoveredURLs;
        Map<String, UniformResourceLocatorDto> tempExploredURLs = exploredURLs;

        if (!discoveredURLs.isEmpty()) {
            discoveredURLs = new ConcurrentHashMap<>();
            LOGGER.info("Publishing " + tempDiscoveredURLs.values().size() + " discovered URLs");
            urlFrontierClient.putAllNew(tempDiscoveredURLs.values());
        }

        if (!exploredURLs.isEmpty()) {
            exploredURLs = new ConcurrentHashMap<>();
            LOGGER.info("Publishing " + tempExploredURLs.values().size() + " explored URLs");
            urlFrontierClient.putAllExplored(tempExploredURLs.values());
        }
    }

    private void publishDocuments() {
        Set<WebDocumentDto> tempExploredDocuments = exploredDocuments;

        if (!exploredDocuments.isEmpty()) {
            exploredDocuments = ConcurrentHashMap.newKeySet();
            LOGGER.debug("Publishing discovered documents.");
            documentHandlerClient.putAll(tempExploredDocuments);
        }
    }
}
