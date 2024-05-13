package com.project.consumerserver.controller;

import com.project.consumerserver.service.KafkaConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaConsumerController {
    private final KafkaConsumerService consumerService;


    @GetMapping("/api/consume")
    public void startConsuming() throws InterruptedException {
        consumerService.consumeMessages("test");
    }
}