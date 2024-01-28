package com.ampaiva.hostfully.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.javafaker.Faker;

@Configuration
public class JavaFakerConfig {

    @Bean
    public Faker faker() {
        return new Faker();
    }
}
