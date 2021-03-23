package com.aeloaiei.dissertation.domainexplorer.service.nosql;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import com.aeloaiei.dissertation.domainexplorer.repository.nosql.UniformResourceLocatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class UniformResourceLocatorService {
    @Autowired
    private UniformResourceLocatorRepository uniformResourceLocatorRepository;

    public List<UniformResourceLocator> getForDomain(String domain, int count) {
        //Pageable pageable = PageRequest.of(0, count, Sort.by(ASC, "lastCrawled"));

        return uniformResourceLocatorRepository.findByDomainOrderByPathDesc(domain);
    }

    public void putAllExplored(Collection<UniformResourceLocator> urls) {
        uniformResourceLocatorRepository.saveAll(urls);
    }

    public void putAllNew(Collection<UniformResourceLocator> urls) {
        for (UniformResourceLocator url : urls) {
            put(url);
        }
    }

    public void put(UniformResourceLocator url) {
        uniformResourceLocatorRepository.findByLocation(url.getLocation())
                .ifPresent(currentUrl ->
                        url.setLastCrawled(getNewest(url.getLastCrawled(), currentUrl.getLastCrawled())));

        uniformResourceLocatorRepository.save(url);
    }

    private LocalDateTime getNewest(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    public int getCountForDomain(String domain) {
        return uniformResourceLocatorRepository.countByDomain(domain);
    }
}
