package com.project.watermelon.controller;

import com.project.watermelon.dto.ticket.PostTicketMessageRequestDto;
import com.project.watermelon.dto.ticket.PostTicketMessageResponseDto;
import com.project.watermelon.dto.ticket.GetTicketPublishAvailabilityResponseDto;
import com.project.watermelon.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/api/availability")
    public GetTicketPublishAvailabilityResponseDto checkTicketPublishAvailability() {
        return ticketService.checkTicketPublishAvailability();
    }

    @PostMapping("/api/message")
    public PostTicketMessageResponseDto produceTicketMessage(@RequestBody PostTicketMessageRequestDto requestDto) {
        return ticketService.produceTicketMessage(requestDto.getUserId());
    }
}
