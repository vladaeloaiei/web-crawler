package com.aeloaiei.dissertation.crawler.impl.repository.nosql;

import com.aeloaiei.dissertation.crawler.impl.model.nosql.Domain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainRepository extends MongoRepository<Domain, String> {
    Optional<Domain> findTopByOrderByLastCrawledAsc();

    Optional<Domain> findByName(String name);
}