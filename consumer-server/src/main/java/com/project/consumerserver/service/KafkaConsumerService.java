package com.project.consumerserver.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
public class KafkaConsumerService {
    @Autowired
    private KafkaConsumer<String, String> kafkaConsumer;


    @Async
    public void consumeMessages(String topicName) throws InterruptedException {

        kafkaConsumer.subscribe(Collections.singletonList(topicName));

        while (true) {
            var records = kafkaConsumer.poll(Duration.ofMillis(300));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("Received message: (key: %s, value: %s, partition: %d, offset: %d)%n",
                        record.key(), record.value(), record.partition(), record.offset());
                if (record.offset() >= 260) {
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
}

