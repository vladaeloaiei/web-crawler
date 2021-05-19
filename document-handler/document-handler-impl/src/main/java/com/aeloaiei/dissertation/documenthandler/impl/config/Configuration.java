package com.aeloaiei.dissertation.documenthandler.impl.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {
    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }
}
