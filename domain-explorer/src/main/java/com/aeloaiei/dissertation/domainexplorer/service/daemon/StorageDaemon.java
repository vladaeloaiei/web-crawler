package com.aeloaiei.dissertation.domainexplorer.service.daemon;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import com.aeloaiei.dissertation.domainexplorer.model.nosql.WebDocument;
import com.aeloaiei.dissertation.domainexplorer.service.nosql.UniformResourceLocatorService;
import com.aeloaiei.dissertation.domainexplorer.service.nosql.WebDocumentService;
import com.aeloaiei.dissertation.domainfeeder.api.clients.DomainFeederClient;
import com.aeloaiei.dissertation.domainfeeder.api.dto.DomainDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.aeloaiei.dissertation.domainexplorer.utils.Configuration.METADATA_UPDATE_WINDOW_IN_MILLISECONDS;

@Service
public class StorageDaemon implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(StorageDaemon.class);

    @Autowired
    private DomainFeederClient domainFeederClient;
    @Autowired
    private UniformResourceLocatorService urlService;
    @Autowired
    private WebDocumentService documentService;

    private Map<String, DomainDto> discoveredDomains = new ConcurrentHashMap<>();
    private Map<String, UniformResourceLocator> discoveredURLs = new ConcurrentHashMap<>();
    private Map<String, DomainDto> exploredDomains = new ConcurrentHashMap<>();
    private Map<String, UniformResourceLocator> exploredURLs = new ConcurrentHashMap<>();
    private Set<WebDocument> exploredDocuments = ConcurrentHashMap.newKeySet();

    public void putDiscoveredDomains(Map<String, DomainDto> domains) {
        discoveredDomains.putAll(domains);
    }

    public void putDiscoveredURLs(Map<String, UniformResourceLocator> urls) {
        discoveredURLs.putAll(urls);
    }

    public void putExploredDomain(DomainDto domain) {
        exploredDomains.put(domain.getName(), domain);
    }

    public void putExploredURL(UniformResourceLocator url) {
        exploredURLs.put(url.getLocation(), url);
    }

    public void putExploredDocument(WebDocument document) {
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
            Thread.sleep(METADATA_UPDATE_WINDOW_IN_MILLISECONDS);
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
            LOGGER.debug("Publishing discovered domains: " + tempDiscoveredDomains.values().toString());
            domainFeederClient.putNewDomains(tempDiscoveredDomains.values());
        }

        if (!exploredDomains.isEmpty()) {
            exploredDomains = new ConcurrentHashMap<>();
            LOGGER.debug("Publishing explored domains: " + tempExploredDomains.values().toString());
            domainFeederClient.putExploredDomains(tempExploredDomains.values());
        }
    }

    private void publishURLs() {
        Map<String, UniformResourceLocator> tempDiscoveredURLs = discoveredURLs;
        Map<String, UniformResourceLocator> tempExploredURLs = exploredURLs;

        if (!discoveredURLs.isEmpty()) {
            discoveredURLs = new ConcurrentHashMap<>();
            LOGGER.info("Publishing discovered URLs: " + tempDiscoveredURLs.values().toString());
            urlService.putAllNew(tempDiscoveredURLs.values());
        }

        if (!exploredURLs.isEmpty()) {
            exploredURLs = new ConcurrentHashMap<>();
            LOGGER.info("Publishing explored URLs: " + tempExploredURLs.values().toString());
            urlService.putAllExplored(tempExploredURLs.values());
        }
    }

    private void publishDocuments() {
        Set<WebDocument> tempExploredDocuments = exploredDocuments;

        if (!exploredDocuments.isEmpty()) {
            exploredDocuments = ConcurrentHashMap.newKeySet();
            LOGGER.debug("Publishing discovered documents.");
            documentService.putAll(tempExploredDocuments);
        }
    }
}
