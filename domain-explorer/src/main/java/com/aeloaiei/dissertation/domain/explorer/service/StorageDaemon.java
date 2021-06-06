package com.aeloaiei.dissertation.domain.explorer.service;

import com.aeloaiei.dissertation.documenthandler.api.clients.DocumentHandlerClient;
import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import com.aeloaiei.dissertation.domain.explorer.config.Configuration;
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
    private UrlFrontierClient urlFrontierClient;
    @Autowired
    private DocumentHandlerClient documentHandlerClient;

    private Map<String, UniformResourceLocatorDto> exploredURLs = new ConcurrentHashMap<>();
    private Set<WebDocumentDto> exploredDocuments = ConcurrentHashMap.newKeySet();

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
        Thread.sleep(config.metadataUpdateWindow);
        publishURLs();
        publishDocuments();
    }

    private void publishURLs() {
        Map<String, UniformResourceLocatorDto> tempExploredURLs = exploredURLs;

        try {
            if (!exploredURLs.isEmpty()) {
                exploredURLs = new ConcurrentHashMap<>();
                LOGGER.info("Publishing " + tempExploredURLs.values().size() + " explored URLs");
                urlFrontierClient.put(tempExploredURLs.values());
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to publish URLs. Error: ", e);
            exploredURLs.putAll(tempExploredURLs);
        }
    }

    private void publishDocuments() {
        Set<WebDocumentDto> tempExploredDocuments = exploredDocuments;

        try {
            if (!exploredDocuments.isEmpty()) {
                exploredDocuments = ConcurrentHashMap.newKeySet();
                LOGGER.debug("Publishing discovered documents.");
                documentHandlerClient.putAll(tempExploredDocuments);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to publish. Error: ", e);
            exploredDocuments.addAll(tempExploredDocuments);
        }
    }
}
