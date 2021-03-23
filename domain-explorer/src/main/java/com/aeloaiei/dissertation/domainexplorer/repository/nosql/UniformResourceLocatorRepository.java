package com.aeloaiei.dissertation.domainexplorer.repository.nosql;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniformResourceLocatorRepository extends MongoRepository<UniformResourceLocator, String> {
    List<UniformResourceLocator> findAllByDomain(String domain, Pageable pageable);

    List<UniformResourceLocator> findByDomainOrderByPathDesc(String domain);

    Optional<UniformResourceLocator> findByLocation(String location);

    int countByDomain(String domain);
}