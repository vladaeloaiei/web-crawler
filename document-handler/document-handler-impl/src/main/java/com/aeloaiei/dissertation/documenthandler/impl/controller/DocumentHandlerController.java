package com.aeloaiei.dissertation.documenthandler.impl.controller;

import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import com.aeloaiei.dissertation.documenthandler.impl.model.nosql.WebDocument;
import com.aeloaiei.dissertation.documenthandler.impl.service.DocumentHandlerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequestMapping("/documenthandler")
@RestController
public class DocumentHandlerController {
    @Autowired
    private DocumentHandlerService documentHandlerService;
    @Autowired
    private ModelMapper modelMapper;

    @PutMapping
    public ResponseEntity<?> putAll(@RequestBody Collection<WebDocumentDto> webDocumentDtos) {
        List<WebDocument> webDocuments = webDocumentDtos.stream()
                .map(webDocumentDto -> modelMapper.map(webDocumentDto, WebDocument.class))
                .collect(toList());

        documentHandlerService.putAll(webDocuments);
        return ResponseEntity.ok().build();
    }
}
