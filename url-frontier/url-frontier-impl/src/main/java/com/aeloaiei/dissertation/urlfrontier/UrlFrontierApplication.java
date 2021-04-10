package com.aeloaiei.dissertation.urlfrontier;

import com.aeloaiei.dissertation.urlfrontier.impl.model.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.service.UrlFrontierService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

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
        urlFrontierService.putAllNew(Stream.of("https://www.bbc.com/sport",
                "https://www.bbc.com/news/science_and_environment",
                "https://www.bbc.com/culture/music",
                "https://www.bbc.com/weather")
                .map(url -> new UniformResourceLocator(url,
                        "https://www.bbc.com",
                        "/sport",
                        LocalDateTime.now(),
                        emptySet(),
                        emptySet()))
                .collect(toList()));
    }
}
