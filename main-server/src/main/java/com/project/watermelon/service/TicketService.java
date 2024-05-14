package com.project.watermelon.service;

import com.project.watermelon.config.KafkaProducerConfig;
import com.project.watermelon.dto.model.TicketMessageResponse;
import com.project.watermelon.dto.ticket.GetTicketPublishAvailabilityResponseDto;
import com.project.watermelon.dto.model.TicketPublishAvailability;
import com.project.watermelon.dto.ticket.PostTicketMessageResponseDto;
import com.project.watermelon.repository.TicketRedisRepository;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRedisRepository ticketRedisRepository;
    private final KafkaProducer<String, String> kafkaProducer;

    @Value("${kafka.ticketTopic}")
    private String ticketTopic;

    @PostConstruct
    public void init() {
        kafkaProducer.initTransactions();  // 서비스 초기화 시에 한 번만 호출
    }

    public GetTicketPublishAvailabilityResponseDto checkTicketPublishAvailability() {
        GetTicketPublishAvailabilityResponseDto responseDto = new GetTicketPublishAvailabilityResponseDto();
        TicketPublishAvailability ticketPublishAvailability = new TicketPublishAvailability();

        // count 가용성
        boolean countAvailability = ticketRedisRepository.checkTicketPublishCountAvailability();

        ticketPublishAvailability.setAvailability(countAvailability);
        responseDto.setData(ticketPublishAvailability);

        return responseDto;
    }

    public PostTicketMessageResponseDto produceTicketMessage(String userId) {
        PostTicketMessageResponseDto responseDto = new PostTicketMessageResponseDto();
        TicketMessageResponse ticketMessageResponse = new TicketMessageResponse();

        try {
            // 트랜잭션 시작
            kafkaProducer.beginTransaction();

            String messageValue = "waitingUser:" + userId;
            ProducerRecord<String, String> record = new ProducerRecord<>(ticketTopic, messageValue);
            kafkaProducer.send(record, (metadata, exception) -> {
                // redis에 ulid 저장

                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println("Message sent to topic " + metadata.topic() + " with offset " + metadata.offset());
                    ticketRedisRepository.storeUserIdWithCurrentTimeScore(userId);
                }
            });

            // 트랜잭션 커밋
            kafkaProducer.commitTransaction();
        } catch (Exception e) {
            // 에러 발생 시 트랜잭션 중단
            kafkaProducer.abortTransaction();
            e.printStackTrace();
        }

        Long waitingRank = ticketRedisRepository.getUserRank(userId);
        ticketMessageResponse.setUserId(userId);
        ticketMessageResponse.setWaitingRank(waitingRank);
        responseDto.setData(ticketMessageResponse);

        return responseDto;
    }
}
