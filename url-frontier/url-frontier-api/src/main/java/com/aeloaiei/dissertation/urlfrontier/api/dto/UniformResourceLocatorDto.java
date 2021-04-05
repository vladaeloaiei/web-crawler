package com.aeloaiei.dissertation.urlfrontier.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.time.LocalDateTime.now;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniformResourceLocatorDto {
    public static final int INVALID_PORT = -1;

    private String location;
    private String domain;
    private String path;
    private LocalDateTime lastCrawled;
    private Set<String> linksReferred;
    private Set<String> domainsReferred;

    public UniformResourceLocatorDto(String link) throws MalformedURLException {
        URL url = new URL(link);

        location = url.toString();
        path = url.getPath();
        domain = buildDomain(url);
        lastCrawled = now();
        linksReferred = new HashSet<>();
        domainsReferred = new HashSet<>();
    }

    private String buildDomain(URL url) {
        String domain = url.getProtocol() + "://" + url.getHost();

        if (url.getPort() != INVALID_PORT) {
            domain += ":" + url.getPort();
        }

        return domain;
    }
}
