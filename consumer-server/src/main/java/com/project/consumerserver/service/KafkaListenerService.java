package com.project.consumerserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.consumerserver.config.KafkaConfig;
import com.project.consumerserver.dto.ReservationMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final KafkaConfig kafkaConfig;
    private final ReservationService reservationService;

    @KafkaListener(topics = "#{@kafkaConfig.getReservationMessageTopic()}", groupId = "#{@kafkaConfig.getReservationMessageGroup()}")
    public void listen(String message, Acknowledgment acknowledgment, ConsumerRecord<String, String> record) {
        long offset = record.offset();
        // message = "concertMappingId:1:waitingUser:rlafbf222@naver.com"
        ReservationMessage reservationMessage = parseReservationMessage(message);
        System.out.println("Received message: " + message + " offset: " + offset + " partition: " + Integer.toString(record.partition()));

        // 메시지 처리 로직
        try {
            boolean isProcessComplete = reservationService.processMessage(reservationMessage);
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(message, ReservationMessage.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid message format: " + message, e);
        }
    }
}
