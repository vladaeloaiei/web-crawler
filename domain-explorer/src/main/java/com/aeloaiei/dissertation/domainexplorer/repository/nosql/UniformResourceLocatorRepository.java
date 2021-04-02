package com.aeloaiei.dissertation.domainexplorer.repository.nosql;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniformResourceLocatorRepository extends MongoRepository<UniformResourceLocator, String> {
    List<UniformResourceLocator> findAllByDomain(String domain, Pageable pageable);

    List<UniformResourceLocator> findByDomainOrderByPathDesc(String domain, Pageable pageable);

    boolean existsByLocation(String location);

    int countByDomain(String domain);
}