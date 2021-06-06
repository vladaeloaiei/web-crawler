package com.aeloaiei.dissertation.domainfeeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class DomainFeederApplication {
    public static void main(String[] args) {
        SpringApplication.run(DomainFeederApplication.class, args);
    }
}
