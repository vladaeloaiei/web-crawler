package com.aeloaiei.dissertation.domainexplorer.service;

import com.aeloaiei.dissertation.domainexplorer.config.Configuration;
import com.aeloaiei.dissertation.domainexplorer.http.HTMLParser;
import com.aeloaiei.dissertation.domainexplorer.http.HTTPResourceRetriever;
import com.aeloaiei.dissertation.domainexplorer.http.RobotsTxtParser;
import com.aeloaiei.dissertation.domainfeeder.api.clients.DomainFeederClient;
import com.aeloaiei.dissertation.urlfrontier.api.clients.UrlFrontierClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MultiDomainExplorerDaemon implements Runnable {

    private Configuration config;
    private DomainFeederClient domainFeederClient;
    private UrlFrontierClient urlFrontierClient;
    private StorageDaemon storageDaemon;
    private HTTPResourceRetriever httpResourceRetriever;
    private HTMLParser htmlParser;
    private RobotsTxtParser robotsTxtParser;
    private ExecutorService executor;

    @Autowired
    public MultiDomainExplorerDaemon(Configuration config,
                                     DomainFeederClient domainFeederClient,
                                     UrlFrontierClient urlFrontierClient,
                                     StorageDaemon storageDaemon,
                                     HTTPResourceRetriever httpResourceRetriever,
                                     HTMLParser htmlParser,
                                     RobotsTxtParser robotsTxtParser) {
        this.config = config;
        this.domainFeederClient = domainFeederClient;
        this.urlFrontierClient = urlFrontierClient;
        this.storageDaemon = storageDaemon;
        this.httpResourceRetriever = httpResourceRetriever;
        this.htmlParser = htmlParser;
        this.robotsTxtParser = robotsTxtParser;
        this.executor = Executors.newFixedThreadPool(config.concurrentDomainsExplorerCount);
    }

    @Override
    public void run() {
        for (int i = 0; i < config.concurrentDomainsExplorerCount; ++i) {
            executor.submit(new SingleDomainExplorerDaemon(
                    config,
                    domainFeederClient,
                    urlFrontierClient,
                    storageDaemon,
                    httpResourceRetriever,
                    htmlParser,
                    robotsTxtParser));
        }
    }
}
