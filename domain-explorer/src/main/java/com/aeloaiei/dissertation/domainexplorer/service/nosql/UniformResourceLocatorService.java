package com.aeloaiei.dissertation.domainexplorer.service.nosql;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import com.aeloaiei.dissertation.domainexplorer.repository.nosql.UniformResourceLocatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UniformResourceLocatorService {
    @Autowired
    private UniformResourceLocatorRepository uniformResourceLocatorRepository;

    public List<UniformResourceLocator> getForDomain(String domain, int count) {
        Pageable pageable = PageRequest.of(0, count);

        return uniformResourceLocatorRepository.findByDomainOrderByPathDesc(domain, pageable);
    }

    public void putAllExplored(Collection<UniformResourceLocator> urls) {
        uniformResourceLocatorRepository.saveAll(urls);
    }

    public void putAllNew(Collection<UniformResourceLocator> urls) {
        for (UniformResourceLocator url : urls) {
            if (!uniformResourceLocatorRepository.existsByLocation(url.getLocation())) {
                uniformResourceLocatorRepository.save(url);
            }
        }
    }

    public int getCountForDomain(String domain) {
        return uniformResourceLocatorRepository.countByDomain(domain);
    }
}
