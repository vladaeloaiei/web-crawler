package com.aeloaiei.dissertation.domainexplorer.config;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${config.user.agent}")
    public String userAgent;

    @Value("${config.metadata.update.window}")
    public int metadataUpdateWindow;

    @Value("${config.resource.request.delay}")
    public int resourceRequestDelay;

    @Value("${config.concurrent.domains.explorer.count}")
    public int concurrentDomainsExplorerCount;
}
