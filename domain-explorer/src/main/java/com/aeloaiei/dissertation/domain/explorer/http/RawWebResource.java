package com.aeloaiei.dissertation.domain.explorer.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawWebResource {
    private String location;
    private String content;
    private boolean isText;
}
