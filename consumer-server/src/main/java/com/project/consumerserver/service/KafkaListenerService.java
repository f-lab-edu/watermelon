package com.project.consumerserver.service;

import com.project.consumerserver.config.KafkaConfig;
import com.project.consumerserver.dto.ReservationMessage;
import com.project.consumerserver.enumeration.ReservationStatus;
import com.project.consumerserver.repository.LocationReader;
import com.project.consumerserver.repository.ReservationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final KafkaConfig kafkaConfig;
    private final StringRedisTemplate stringRedisTemplate;
    private final LocationReader locationReader;
    private final ReservationRedisRepository reservationRedisRepository;

    @KafkaListener(topics = "#{@kafkaConfig.getReservationMessageTopic()}", groupId = "#{@kafkaConfig.getReservationMessageGroup()}")
    public void listen(String message, Acknowledgment acknowledgment, ConsumerRecord<String, String> record) {long offset = record.offset();
        // message = "concertMappingId:1:waitingUser:rlafbf222@naver.com:locationId:1"
//        acknowledgment.acknowledge();

        ReservationMessage reservationMessage = parseReservationMessage(message);
        System.out.println("Received message: " + message + " offset: " + offset + " partition: " + Integer.toString(record.partition()));

        // 메시지 처리 로직
        try {
            boolean isProcessComplete = processMessage(reservationMessage);
            if (isProcessComplete) {
                acknowledgment.acknowledge(); // 성공적으로 메시지를 처리한 후 수동으로 커밋
                System.out.println("Message was committed");
            } else {
                System.out.println("Message was not committed");
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message);
            // 예외 처리 로직, 필요한 경우 재처리 로직 구현
        }
    }

    private ReservationMessage parseReservationMessage(String message) {
        // message format: "concertMappingId:1:waitingUser:rlafbf222@naver.com:locationId:1"
        String[] parts = message.split("(:)");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid message format: " + message);
        }

        Long concertMappingId;
        try {
            concertMappingId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid concertMappingId format: " + parts[1]);
        }

        String memberEmail = parts[3];

        Long locationId;
        try {
            locationId = Long.parseLong(parts[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid locationId format: " + parts[3]);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("index out of bounds");
        }

        return new ReservationMessage(concertMappingId, memberEmail, locationId);
    }

    private boolean processMessage(ReservationMessage message) {
        try {
            String stringConcertMappingId = Long.toString(message.getConcertMappingId());
            Long seatCapacity = retrieveSeatCapacity(stringConcertMappingId, message.getLocationId());

            if (seatCapacity > 0) {
                // update status: AVAILABLE
                reservationRedisRepository.updateMemberStatus(stringConcertMappingId, message.getMemberEmail(), ReservationStatus.AVAILABLE);

                // atomic decrement seat capacity
                reservationRedisRepository.decrementConcertMappingSeatCapacity(stringConcertMappingId);

                // delete zset data
                reservationRedisRepository.deleteUserRank(stringConcertMappingId, message.getMemberEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private Long retrieveSeatCapacity(String stringConcertMappingId, Long locationId) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        String key = "concertMappingId:" + stringConcertMappingId;
        String hKey = "seatCapacity";
        String stringSeatCapacity = hashOps.get(key, hKey);
        Long seatCapacity;

        if (stringSeatCapacity != null) {
            seatCapacity = Long.parseLong(stringSeatCapacity);
        } else {
            seatCapacity = locationReader.retrieveLocationSeatCapacity(locationId);
            reservationRedisRepository.storeConcertMappingSeatCapacity(stringConcertMappingId, seatCapacity);
        }
        return seatCapacity;
    }
}
