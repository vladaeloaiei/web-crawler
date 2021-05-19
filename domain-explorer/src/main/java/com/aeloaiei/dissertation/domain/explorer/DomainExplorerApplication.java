package com.aeloaiei.dissertation.domain.explorer;


import com.aeloaiei.dissertation.domain.explorer.service.MultiDomainExplorerDaemon;
import com.aeloaiei.dissertation.domain.explorer.service.StorageDaemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableFeignClients(basePackages = {
        "com.aeloaiei.dissertation.urlfrontier.api",
        "com.aeloaiei.dissertation.documenthandler.api"
})
@SpringBootApplication
public class DomainExplorerApplication implements CommandLineRunner {

    @Autowired
    private MultiDomainExplorerDaemon multiDomainExplorerDaemon;
    @Autowired
    private StorageDaemon storageDaemon;

    public static void main(String[] args) {
        SpringApplication.run(DomainExplorerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(multiDomainExplorerDaemon);
        executor.submit(storageDaemon);
    }
}
