package com.aeloaiei.dissertation.urlfrontier;

import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import com.aeloaiei.dissertation.urlfrontier.impl.config.Configuration;
import com.aeloaiei.dissertation.urlfrontier.impl.model.UniformResourceLocator;
import com.aeloaiei.dissertation.urlfrontier.impl.service.UrlFrontierService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

@SpringBootApplication
public class UrlFrontierApplication {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Configuration config;

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
    public void addURLs() throws Exception {
        try {
            List<UniformResourceLocator> urls = new HashSet<>(Files.readAllLines(Paths.get(config.startupUrlsPath)))
                    .stream()
                    .map(this::mapStringToURLDto)
                    .map(urlDto -> modelMapper.map(urlDto, UniformResourceLocator.class))
                    .collect(toList());

            urlFrontierService.putAllNew(urls);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the startup_urls.txt config file", e);
        }
    }

    private UniformResourceLocatorDto mapStringToURLDto(String url) {
        try {
            return new UniformResourceLocatorDto(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
