package com.aeloaiei.dissertation.urlfrontier.impl.repository;

import com.aeloaiei.dissertation.urlfrontier.impl.model.UniformResourceLocator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniformResourceLocatorRepository extends MongoRepository<UniformResourceLocator, String> {
    List<UniformResourceLocator> findByDomainOrderByLastCrawledAsc(String domain, Pageable pageable);

    boolean existsByLocation(String location);

    int countByDomain(String domain);
}
