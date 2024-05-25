package com.project.watermelon.service;

import com.project.watermelon.dto.model.ReservationMessageResponse;
import com.project.watermelon.dto.reservation.ReservationMessageResponseDto;
import com.project.watermelon.exception.MemberAlreadyRequestReservationException;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ReservationRedisRepository;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRedisRepository reservationRedisRepository;
    private final KafkaProducer<String, String> kafkaProducer;
    private final StringRedisTemplate stringRedisTemplate;
    private final ConcertMappingRepository concertMappingRepository;

    @Value("${kafka.reservationMessageTopic}")
    private String reservationMessageTopic;

    @PostConstruct
    public void init() {
        kafkaProducer.initTransactions();  // 서비스 초기화 시에 한 번만 호출
    }

    @Transactional
    public ReservationMessageResponseDto produceReservationMessage(String memberEmail, Long concertMappingId) {
        ConcertMapping concertMapping = concertMappingRepository.findByConcertMappingId(concertMappingId).orElseThrow(
                () -> new IllegalArgumentException("concert mapping not exist")
        );
        Long locationId = concertMapping.getLocation().getLocationId();

        String stringConcertMappingId = Long.toString(concertMappingId);
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        String key = "concertMappingId:" + stringConcertMappingId + ":memberStatus";

        // 중복 체크
        Boolean isMemberExist = hashOps.hasKey(key, memberEmail);
        if (isMemberExist) {
            String message = "Member " + memberEmail + " is already registered for concert: " + stringConcertMappingId;
            System.out.println(message);
            throw new MemberAlreadyRequestReservationException(message);
        }

        try {
            // 트랜잭션 시작
            kafkaProducer.beginTransaction();

            String messageValue = "concertMappingId:" + stringConcertMappingId + ":"+ "waitingUser:" + memberEmail + ":" + "locationId:" + locationId;
            ProducerRecord<String, String> record = new ProducerRecord<>(reservationMessageTopic, stringConcertMappingId, messageValue);
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

        Long memberRank = reservationRedisRepository.getUserRank(memberEmail, stringConcertMappingId);
        ReservationMessageResponse response = new ReservationMessageResponse(memberEmail, memberRank);
        return new ReservationMessageResponseDto(response);
    }
}
