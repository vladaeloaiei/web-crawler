package com.aeloaiei.dissertation.domainexplorer.model.nosql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.HashIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "web-documents")
public class WebDocument {
    @MongoId
    private String location;
    private String content;
    @HashIndexed
    private int httpStatus;
}
