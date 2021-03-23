package com.aeloaiei.dissertation.crawler.api.clients;

import com.aeloaiei.dissertation.crawler.api.dto.DomainDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(value = "CrawlClient", url = "http://localhost:8081/crawler")
public interface CrawlClient {
    @GetMapping
    DomainDto getCrawlableDomain();

    @PutMapping("/explored")
    void putExploredDomains(@RequestBody Collection<DomainDto> domainDtos);

    @PutMapping("/new")
    void putNewDomains(@RequestBody Collection<DomainDto> domainDtos);
}
