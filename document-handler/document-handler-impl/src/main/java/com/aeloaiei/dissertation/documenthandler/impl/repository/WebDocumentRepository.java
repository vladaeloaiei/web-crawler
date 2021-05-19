package com.aeloaiei.dissertation.documenthandler.impl.repository;

import com.aeloaiei.dissertation.documenthandler.impl.model.WebDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebDocumentRepository extends MongoRepository<WebDocument, String> {
}
