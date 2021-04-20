package com.aeloaiei.dissertation.domainexplorer.service;

import com.aeloaiei.dissertation.domainexplorer.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiDomainExplorerDaemon implements Runnable {

    @Autowired
    private Configuration config;

    private final ExecutorService executor = Executors.newFixedThreadPool(config.concurrentDomainsExplorerCount);

    @Override
    public void run() {
        for (int i = 0; i < config.concurrentDomainsExplorerCount; ++i) {
            executor.submit(new SingleDomainExplorerDaemon());
        }
    }
}
