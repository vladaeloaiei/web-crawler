package com.aeloaiei.dissertation.domainfeeder.api.clients;

import com.aeloaiei.dissertation.domainfeeder.api.dto.DomainDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(value = "DomainFeederClient", url = "http://localhost:9001/domainfeeder")
public interface DomainFeederClient {
    @GetMapping
    DomainDto getCrawlableDomain();

    @PutMapping("/explored")
    void putExploredDomains(@RequestBody Collection<DomainDto> domainDtos);

    @PutMapping("/new")
    void putNewDomains(@RequestBody Collection<DomainDto> domainDtos);
}
