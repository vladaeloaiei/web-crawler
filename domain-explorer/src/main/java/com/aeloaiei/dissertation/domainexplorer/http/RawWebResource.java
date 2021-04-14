package com.aeloaiei.dissertation.domainexplorer.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawWebResource {
    private String location;
    private String content;
}
