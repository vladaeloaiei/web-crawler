package com.aeloaiei.dissertation.domainfeeder;

import com.aeloaiei.dissertation.domainfeeder.impl.model.nosql.Domain;
import com.aeloaiei.dissertation.domainfeeder.impl.service.nosql.DomainFeederService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;

@SpringBootApplication
public class DomainFeederApplication {

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
    public void addDomain() {
        domainFeederService.putAllNew(singletonList(new Domain("https://www.baeldung.com", now())));
    }
}
