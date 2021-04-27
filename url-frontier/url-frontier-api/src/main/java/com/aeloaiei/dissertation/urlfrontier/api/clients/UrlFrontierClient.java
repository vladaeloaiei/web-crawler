package com.aeloaiei.dissertation.urlfrontier.api.clients;

import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient(name = "UrlFrontierClient", url = "${feign.client.url.frontier.url}/urlfrontier")
public interface UrlFrontierClient {
    @PutMapping("/explored")
    public void putAllExplored(@RequestBody Collection<UniformResourceLocatorDto> urlDtos);

    @PutMapping("/new")
    public void putAllNew(@RequestBody Collection<UniformResourceLocatorDto> urlDtos);

    @GetMapping
    public Collection<UniformResourceLocatorDto> getExplorableURLs(@RequestParam String domain);
}
