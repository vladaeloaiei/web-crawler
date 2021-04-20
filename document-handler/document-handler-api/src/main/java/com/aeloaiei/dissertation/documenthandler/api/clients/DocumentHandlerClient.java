package com.aeloaiei.dissertation.documenthandler.api.clients;

import com.aeloaiei.dissertation.documenthandler.api.dto.WebDocumentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(value = "DocumentHandlerClient", url = "${feign.client.document.handler.url}") //http://localhost:9003/documenthandler
public interface DocumentHandlerClient {
    @PutMapping
    public void putAll(@RequestBody  Collection<WebDocumentDto> webDocuments);
}
