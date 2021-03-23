package com.aeloaiei.dissertation.domainexplorer.http;

import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawWebResource {
    private UniformResourceLocator url;
    private String content;
    private HttpStatus status;
}
