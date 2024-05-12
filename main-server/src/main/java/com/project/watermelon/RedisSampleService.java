package com.project.watermelon;

import com.project.watermelon.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisSampleService {

    private final RedisUtil redisUtil;

    public void issueMessage(String messageId, Object message) {
        // 티켓 정보를 Redis에 저장
        redisUtil.saveMessage(messageId, message);
    }

    public Object retrieveTicket(String ticketId) {
        // 티켓 정보를 Redis에서 조회
        return redisUtil.getMessage(ticketId);
    }

    public void issueTicketWithExpiry(String ticketId, Object ticketDetails, long timeout) {
        // 티켓 정보를 일정 시간 후 만료되도록 Redis에 저장
        redisUtil.saveMessageWithExpiration(ticketId, ticketDetails, timeout, TimeUnit.HOURS);
    }

}
