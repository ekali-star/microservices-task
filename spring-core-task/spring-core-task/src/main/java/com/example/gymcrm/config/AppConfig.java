package com.example.gymcrm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.example.gymcrm")
@PropertySource("classpath:application.properties")
@Import(PersistenceConfig.class)
@EnableJpaRepositories(basePackages = "com.example.gymcrm.repository")
public class AppConfig {
}