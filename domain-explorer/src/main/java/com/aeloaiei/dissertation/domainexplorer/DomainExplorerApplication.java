package com.aeloaiei.dissertation.domainexplorer;


import com.aeloaiei.dissertation.domainexplorer.model.nosql.UniformResourceLocator;
import com.aeloaiei.dissertation.domainexplorer.service.daemon.SingleDomainExplorerDaemon;
import com.aeloaiei.dissertation.domainexplorer.service.daemon.StorageDaemon;
import com.aeloaiei.dissertation.domainexplorer.service.nosql.UniformResourceLocatorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.singletonList;

@EnableFeignClients(basePackages = "com.aeloaiei.dissertation.domainfeeder.api")
@EnableMongoRepositories(basePackages = "com.aeloaiei.dissertation.domainexplorer.repository.nosql")
@SpringBootApplication
public class DomainExplorerApplication implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(DomainExplorerApplication.class);

    @Autowired
    private SingleDomainExplorerDaemon singleDomainExplorerDaemon;
    @Autowired
    private StorageDaemon storageDaemon;
    @Autowired
    private UniformResourceLocatorService uniformResourceLocatorService;

    public static void main(String[] args) {
        SpringApplication.run(DomainExplorerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(singleDomainExplorerDaemon);
        executor.submit(storageDaemon);
    }

    @PostConstruct
    public void addURL() throws Exception {
        uniformResourceLocatorService.putAllNew(singletonList(new UniformResourceLocator("https://www.baeldung.com/java-initialize-hashmap")));
    }
}
