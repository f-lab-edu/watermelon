package com.project.watermelon.controller;

import com.project.watermelon.dto.TicketPublishAvailabilityResponseDto;
import com.project.watermelon.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/api/availability")
    public TicketPublishAvailabilityResponseDto checkTicketPublishAvailability() {
        return ticketService.checkTicketPublishAvailability();
    }
}
