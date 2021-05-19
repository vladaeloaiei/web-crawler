package com.aeloaiei.dissertation.domainfeeder.api.clients;

import com.aeloaiei.dissertation.domainfeeder.api.dto.DomainDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(name = "DomainFeederClient", url = "${feign.client.domain.feeder.url}/domainfeeder")
public interface DomainFeederClient {
    @GetMapping
    DomainDto getCrawlableDomain();

    @PutMapping("/new")
    Collection<DomainDto> put(@RequestBody Collection<DomainDto> domainDtos);
}
