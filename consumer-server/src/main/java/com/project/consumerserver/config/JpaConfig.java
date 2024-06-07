package com.project.consumerserver.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.project.watermelon")
@EntityScan(basePackages = "com.project.watermelon.model")
@Configuration
public class JpaConfig {
}
