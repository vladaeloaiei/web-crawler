package com.aeloaiei.dissertation.urlfrontier.impl.model.nosql;

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

import java.time.LocalDateTime;
import java.util.Set;

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
    //TODO add title
}
