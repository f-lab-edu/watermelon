package com.project.consumerserver.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private Boolean consumingFlag = true;

    @Async
    public void consumeMessages(String topicName) throws InterruptedException {

        kafkaConsumer.subscribe(Collections.singletonList(topicName));

        while (consumingFlag) {
            var records = kafkaConsumer.poll(Duration.ofMillis(300));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("Received message: (key: %s, value: %s, partition: %d, offset: %d)%n",
                        record.key(), record.value(), record.partition(), record.offset());
                // 임시코드는 PR로 올릴 필요 없음 / 샘플 코드 정리!
                // 카프카 리스너 (어노테이션)
                if (record.offset() >= 400) {
                    Thread.sleep(500);
                    kafkaConsumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
                    break;
                }

                // 오프셋 커밋
                kafkaConsumer.commitSync();
                System.out.println("Committed offset: " + Long.toString(record.offset()));
            }
            System.out.println("loop ended successfully");
            Thread.sleep(5000);
        }
    }

    @KafkaListener(topics = "test", groupId = "test-group")
    public void listen(String message, Acknowledgment acknowledgment, ConsumerRecord<String, String> record) {
        long offset = record.offset();
        System.out.println("Received message: " + message + "offset: " + Long.toString(offset));
        // 메시지 처리 로직
        try {
            boolean isProcessComplete = processMessage(offset, message);
            if (isProcessComplete) {
                acknowledgment.acknowledge(); // 성공적으로 메시지를 처리한 후 수동으로 커밋
            }
            else {
                System.out.println("message was not committed");
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + message);
            // 예외 처리 로직, 필요한 경우 재처리 로직 구현
        }
    }

    private boolean processMessage(Long offset, String message) {
        System.out.println("message processing complete" + message);
        return offset <= 293;
    }
}
