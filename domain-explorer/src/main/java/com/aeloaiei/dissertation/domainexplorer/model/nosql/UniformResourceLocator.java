package com.aeloaiei.dissertation.domainexplorer.model.nosql;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.HashIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.time.LocalDateTime.now;

/**
 * This is a {@link java.net.URL} wrapper
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "web-urls")
public class UniformResourceLocator {
    public static final int INVALID_PORT = -1;

    @MongoId
    private String location;
    @HashIndexed
    private String domain;
    private String path;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastCrawled;
    private Set<String> linksReferred;
    private Set<String> domainsReferred;

    public UniformResourceLocator(String link) throws MalformedURLException {
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
