package com.project.watermelon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.exception.MemberAlreadyRequestReservationException;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ReservationRedisRepository;
import com.project.watermelon.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRedisRepository reservationRedisRepository;
    private final ReservationRepository reservationRepository;
    private final KafkaProducer<String, String> kafkaProducer;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${kafka.reservationMessageTopic}")
    private String reservationMessageTopic;

    @PostConstruct
    public void init() {
        kafkaProducer.initTransactions();  // 서비스 초기화 시에 한 번만 호출
    }

    @Transactional
    public CommonBackendResponseDto<String> produceReservationMessage(String memberEmail, Long concertMappingId) {
        String stringConcertMappingId = Long.toString(concertMappingId);

        if (isMemberExists(stringConcertMappingId, memberEmail)) {
            String message = "Member " + memberEmail + " is already registered for concert: " + stringConcertMappingId;
            System.out.println(message);
            throw new MemberAlreadyRequestReservationException(message);
        }
        try {
            // 트랜잭션 시작
            kafkaProducer.beginTransaction();
            // Jackson ObjectMapper 인스턴스 생성
            ProducerRecord<String, String> record = transformMessageStringToJson(memberEmail, stringConcertMappingId);
            // Future 객체를 통해 send() 결과 확인
            Future<RecordMetadata> sendFuture = kafkaProducer.send(record);

            // 메시지 전송 완료 까지 대기 (Timeout 1sec)
            RecordMetadata metadata = sendFuture.get(1, TimeUnit.SECONDS);

            // 트랜잭션 커밋
            kafkaProducer.commitTransaction();

            reservationRedisRepository.storeUserIdWithDefaultState(memberEmail, stringConcertMappingId);
            System.out.println("Message sent to topic " + metadata.topic() + " with offset " + metadata.offset());
        } catch (TimeoutException e) {
            System.out.println("Timeout while waiting for message send to complete");
            kafkaProducer.abortTransaction();
        } catch (ExecutionException e) {
            System.out.println("Execution exception while sending message");
            kafkaProducer.abortTransaction();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for message send to complete");
            kafkaProducer.abortTransaction();
            Thread.currentThread().interrupt();  // Restore interrupted status
        } catch (Exception e) {
            // 기타 예외 발생 시 트랜잭션 중단
            kafkaProducer.abortTransaction();
            e.printStackTrace();
        }

        return new CommonBackendResponseDto<>();
    }

    private ProducerRecord<String, String> transformMessageStringToJson(String memberEmail, String stringConcertMappingId) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 데이터를 담을 Map 생성
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("concertMappingId", stringConcertMappingId);
        messageMap.put("memberEmail", memberEmail);

        try {
            // Map을 JSON 문자열로 변환
            String messageValue = objectMapper.writeValueAsString(messageMap);
            return new ProducerRecord<>(reservationMessageTopic, stringConcertMappingId, messageValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to serialize message to JSON", e);
        }
    }

    private Boolean isMemberExists(String stringConcertMappingId, String memberEmail) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        String key = "concertMappingId:" + stringConcertMappingId + ":memberStatus";

        // 중복 체크
        boolean isMemberExists = hashOps.hasKey(key, memberEmail);

        if (!isMemberExists) {
            Optional<Reservation> reservation = reservationRepository.findByMember_Email(memberEmail);
            if (reservation.isPresent()) {
                reservationRedisRepository.storeUserIdWithDefaultState(memberEmail, stringConcertMappingId);
                isMemberExists = true;
            }
        }
        return isMemberExists;
    }
}
