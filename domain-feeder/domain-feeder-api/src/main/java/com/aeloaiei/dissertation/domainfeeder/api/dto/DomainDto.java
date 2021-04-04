package com.aeloaiei.dissertation.domainfeeder.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainDto {
    private String name;
    private LocalDateTime lastCrawled;
}
