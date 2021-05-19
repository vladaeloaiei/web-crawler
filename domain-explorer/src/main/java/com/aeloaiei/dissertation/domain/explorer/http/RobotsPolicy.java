package com.aeloaiei.dissertation.domain.explorer.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotsPolicy {
    private String userAgent;
    private Set<String> allow;
    private Set<String> disallow;

    public RobotsPolicy(String userAgent) {
        this.userAgent = userAgent;
        allow = new HashSet<>();
        disallow = new HashSet<>();
    }
}
