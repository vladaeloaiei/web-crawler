package com.aeloaiei.dissertation.documenthandler.impl.repository.nosql;

import com.aeloaiei.dissertation.documenthandler.impl.model.nosql.WebDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebDocumentRepository extends MongoRepository<WebDocument, String> {
}
