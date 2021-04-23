package com.aeloaiei.dissertation.urlfrontier.impl.config;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${config.crawl.percentage}")
    public double crawlPercentage;

    @Value("${config.allowed.domains.path}")
    public String allowedDomainsFilePath;

    @Value("${config.startup.urls.path}")
    public String startupUrlsPath;
}
