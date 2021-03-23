package com.aeloaiei.dissertation.domainexplorer.service.nosql;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.WebDocument;
import com.aeloaiei.dissertation.domainexplorer.repository.nosql.WebDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class WebDocumentService {
    @Autowired
    private WebDocumentRepository webDocumentRepository;


    public void putAll(Collection<WebDocument> webDocuments) {
        webDocumentRepository.saveAll(webDocuments);
    }
}
