package com.aeloaiei.dissertation.crawler.impl.controller;

import com.aeloaiei.dissertation.crawler.api.dto.DomainDto;
import com.aeloaiei.dissertation.crawler.impl.model.nosql.Domain;
import com.aeloaiei.dissertation.crawler.impl.service.nosql.CrawlerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequestMapping("/crawler")
@RestController
public class CrawlerController {
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<DomainDto> getCrawlableDomains() {
        return crawlerService.getCrawlableDomain()
                .map(d -> modelMapper.map(d, DomainDto.class))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/explored")
    public ResponseEntity<Collection<DomainDto>> putExploredDomains(@RequestBody Collection<DomainDto> domainDtos) {
        List<Domain> domains = domainDtos
                .stream()
                .map(domainDto -> modelMapper.map(domainDto, Domain.class))
                .collect(toList());
        List<DomainDto> savedDomainDtos = crawlerService.putAllExplored(domains)
                .stream()
                .map(domain -> modelMapper.map(domain, DomainDto.class))
                .collect(toList());

        return ResponseEntity.ok(savedDomainDtos);
    }

    @PutMapping("/new")
    public ResponseEntity<Collection<DomainDto>> putNewDomains(@RequestBody Collection<DomainDto> domainDtos) {
        List<Domain> domains = domainDtos
                .stream()
                .map(domainDto -> modelMapper.map(domainDto, Domain.class))
                .collect(toList());
        List<DomainDto> savedDomainDtos = crawlerService.putAllNew(domains)
                .stream()
                .map(domain -> modelMapper.map(domain, DomainDto.class))
                .collect(toList());

        return ResponseEntity.ok(savedDomainDtos);
    }
}
