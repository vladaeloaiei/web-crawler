package com.aeloaiei.dissertation.documenthandler.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebDocumentDto {
    private String location;
    private String content;
    private int httpStatus;
}
