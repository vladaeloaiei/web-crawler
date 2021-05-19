package com.aeloaiei.dissertation.domainfeeder.impl.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@org.springframework.context.annotation.Configuration
public class Configuration {

    public static final int RETRY_MIN_DELAY = 1000;
    public static final int RETRY_MAX_DELAY = 10000;
    public static final int RETRY_MAX_ATTEMPTS = 10;

    @Value("${config.allowed.domains.path}")
    public String allowedDomainsFilePath;

    @Value("${config.startup.domains.path}")
    public String startupDomainsPath;

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }
}
