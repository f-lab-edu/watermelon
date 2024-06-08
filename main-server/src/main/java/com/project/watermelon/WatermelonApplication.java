package com.project.watermelon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WatermelonApplication {
	public static void main(String[] args) {
		SpringApplication.run(WatermelonApplication.class, args);
	}
}
