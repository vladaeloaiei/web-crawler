package com.aeloaiei.dissertation.domainexplorer.repository.nosql;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.WebDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebDocumentRepository extends MongoRepository<WebDocument, String> {
}
