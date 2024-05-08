package com.project.watermelon;

import com.project.watermelon.util.RedisUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WatermelonApplication {

	public static void main(String[] args) {
//		SpringApplication.run(WatermelonApplication.class, args);

		ApplicationContext ctx = SpringApplication.run(WatermelonApplication.class, args);
		RedisUtil redisUtil = ctx.getBean(RedisUtil.class);

		// 예제: 메시지 저장 및 검색 테스트
		String messageId = "uniqueMessageId";
		String messageDetails = "This is a test message.";
		redisUtil.saveMessage(messageId, messageDetails);

		// 메시지 검색
		Object retrievedMessage = redisUtil.getMessage(messageId);
		System.out.println("Retrieved Message: " + retrievedMessage);
	}

}
