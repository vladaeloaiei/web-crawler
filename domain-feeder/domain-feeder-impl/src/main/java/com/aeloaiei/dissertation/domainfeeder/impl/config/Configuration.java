package com.aeloaiei.dissertation.domainfeeder.impl.config;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${config.allowed.domains.path}")
    public String allowedDomainsFilePath;
}
