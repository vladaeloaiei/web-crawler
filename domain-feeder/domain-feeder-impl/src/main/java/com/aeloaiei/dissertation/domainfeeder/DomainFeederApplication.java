package com.aeloaiei.dissertation.domainfeeder;

import com.aeloaiei.dissertation.domainfeeder.impl.config.Configuration;
import com.aeloaiei.dissertation.domainfeeder.impl.model.Domain;
import com.aeloaiei.dissertation.domainfeeder.impl.service.DomainFeederService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@SpringBootApplication
public class DomainFeederApplication {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Configuration config;
    @Autowired
    private DomainFeederService domainFeederService;

    public static void main(String[] args) {
        SpringApplication.run(DomainFeederApplication.class, args);
    }

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @PostConstruct
    public void addDomains() throws Exception {
        try {
            List<Domain> domains = new HashSet<>(Files.readAllLines(Paths.get(config.startupDomainsPath)))
                    .stream()
                    .map(domain -> new Domain(domain, now()))
                    .collect(toList());

            domainFeederService.putAllNew(domains);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the startup_domains.txt config file", e);
        }
    }
}
