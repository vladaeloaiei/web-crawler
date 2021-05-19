package com.aeloaiei.dissertation.urlfrontier.api.clients;

import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;

@FeignClient(name = "UrlFrontierClient", url = "${feign.client.url.frontier.url}/urlfrontier")
public interface UrlFrontierClient {
    @GetMapping
    public List<UniformResourceLocatorDto> getExplorableURLs();

    @PutMapping("/explored")
    public void put(@RequestBody Collection<UniformResourceLocatorDto> urlDtos);
}
