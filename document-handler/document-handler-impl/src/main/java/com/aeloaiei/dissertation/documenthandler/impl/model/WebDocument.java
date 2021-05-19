package com.aeloaiei.dissertation.documenthandler.impl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "web-documents")
public class WebDocument {
    @MongoId
    private String location;
    private String title;
    private String content;
}
