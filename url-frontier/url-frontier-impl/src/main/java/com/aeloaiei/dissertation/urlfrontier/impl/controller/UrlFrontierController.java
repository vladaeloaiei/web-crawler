package com.aeloaiei.dissertation.urlfrontier.impl.controller;

import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import com.aeloaiei.dissertation.urlfrontier.impl.model.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.service.UrlFrontierService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequestMapping("/urlfrontier")
@RestController
public class UrlFrontierController {
    @Autowired
    private UrlFrontierService urlFrontierService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Collection<UniformResourceLocatorDto>> getExplorableURLs() {
        List<UniformResourceLocatorDto> urlDtos = urlFrontierService.getExplorableURLs()
                .stream()
                .map(url -> modelMapper.map(url, UniformResourceLocatorDto.class))
                .collect(toList());

        return ResponseEntity.ok(urlDtos);
    }

    @PutMapping("/explored")
    public ResponseEntity<?> put(@RequestBody Collection<UniformResourceLocatorDto> urlDtos) {
        List<UniformResourceLocator> urls = urlDtos.stream()
                .map(urlDto -> modelMapper.map(urlDto, UniformResourceLocator.class))
                .collect(toList());

        urlFrontierService.put(urls);
        return ResponseEntity.ok().build();
    }
}
