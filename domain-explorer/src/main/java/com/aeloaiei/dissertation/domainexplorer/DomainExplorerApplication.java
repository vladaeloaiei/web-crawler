package com.aeloaiei.dissertation.domainexplorer;


import com.aeloaiei.dissertation.domainexplorer.service.SingleDomainExplorerDaemon;
import com.aeloaiei.dissertation.domainexplorer.service.StorageDaemon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableFeignClients(basePackages = {
        "com.aeloaiei.dissertation.domainfeeder.api",
        "com.aeloaiei.dissertation.urlfrontier.api",
        "com.aeloaiei.dissertation.documenthandler.api"
})
@SpringBootApplication
public class DomainExplorerApplication implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(DomainExplorerApplication.class);

    @Autowired
    private SingleDomainExplorerDaemon singleDomainExplorerDaemon;
    @Autowired
    private StorageDaemon storageDaemon;

    public static void main(String[] args) {
        SpringApplication.run(DomainExplorerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(singleDomainExplorerDaemon);
        executor.submit(storageDaemon);
    }
}
