package com.project.watermelon;

import com.project.watermelon.util.RedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@EnableJpaAuditing
@SpringBootApplication
public class WatermelonApplication {
	public static void main(String[] args) {
		SpringApplication.run(WatermelonApplication.class, args);
	}
}
