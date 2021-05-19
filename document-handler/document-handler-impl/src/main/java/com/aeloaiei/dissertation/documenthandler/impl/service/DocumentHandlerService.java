package com.aeloaiei.dissertation.documenthandler.impl.service;

import com.aeloaiei.dissertation.documenthandler.impl.model.WebDocument;
import com.aeloaiei.dissertation.documenthandler.impl.repository.WebDocumentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class DocumentHandlerService {
    private static final Logger LOGGER = LogManager.getLogger(DocumentHandlerService.class);

    @Autowired
    private WebDocumentRepository webDocumentRepository;

    public void putAll(Collection<WebDocument> webDocuments) {
        List<String> documentsName = webDocuments.stream()
                .map(WebDocument::getLocation)
                .collect(toList());

        LOGGER.info("Saving documents: " + documentsName);
        webDocumentRepository.saveAll(webDocuments);
    }
}
