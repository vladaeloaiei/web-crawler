package com.aeloaiei.dissertation.crawler;

import com.aeloaiei.dissertation.crawler.impl.model.nosql.Domain;
import com.aeloaiei.dissertation.crawler.impl.service.nosql.CrawlerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;

@SpringBootApplication
public class CrawlerApplication {

    @Autowired
    private CrawlerService crawlerService;

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @PostConstruct
    public void addDomain() {
        crawlerService.putAllNew(singletonList(new Domain("https://www.baeldung.com", now())));
    }
}
