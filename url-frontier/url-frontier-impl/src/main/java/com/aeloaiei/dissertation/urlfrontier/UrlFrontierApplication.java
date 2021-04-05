package com.aeloaiei.dissertation.urlfrontier;

import com.aeloaiei.dissertation.urlfrontier.impl.model.nosql.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.service.UrlFrontierService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

@SpringBootApplication
public class UrlFrontierApplication {

    @Autowired
    private UrlFrontierService urlFrontierService;

    public static void main(String[] args) {
        SpringApplication.run(UrlFrontierApplication.class, args);
    }

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @PostConstruct
    public void addURL() throws Exception {
        urlFrontierService.putAllNew(singletonList(
                new UniformResourceLocator("https://www.baeldung.com/java-initialize-hashmap",
                        "https://www.baeldung.com",
                        "/java-initialize-hashmap",
                        LocalDateTime.now(),
                        emptySet(),
                        emptySet())));
    }
}
