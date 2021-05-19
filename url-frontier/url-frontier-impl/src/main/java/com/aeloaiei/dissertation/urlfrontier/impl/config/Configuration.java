package com.aeloaiei.dissertation.urlfrontier.impl.config;

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

    @Value("${config.crawl.percentage}")
    public double crawlPercentage;

    @Value("${config.startup.urls.path}")
    public String startupUrlsPath;

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }
}
