package com.aeloaiei.dissertation.domainexplorer.service.daemon;

import com.aeloaiei.dissertation.crawler.api.clients.CrawlClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aeloaiei.dissertation.domainexplorer.utils.Configuration.CONCURRENT_DOMAINS_EXPLORER_COUNT;

public class MultiDomainExplorerDaemon implements Runnable {

    @Autowired
    private CrawlClient crawlClient;

    private ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_DOMAINS_EXPLORER_COUNT);

    @Override
    public void run() {
        for (int i = 0; i < CONCURRENT_DOMAINS_EXPLORER_COUNT; ++i) {
            executor.submit(new SingleDomainExplorerDaemon());
        }
    }
}
