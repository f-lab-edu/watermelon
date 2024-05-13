package com.project.watermelon.service;

import com.project.watermelon.dto.TicketPublishAvailabilityResponseDto;
import com.project.watermelon.dto.model.TicketPublishAvailability;
import com.project.watermelon.repository.TicketRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRedisRepository ticketRedisRepository;
    public TicketPublishAvailabilityResponseDto checkTicketPublishAvailability() {
        TicketPublishAvailabilityResponseDto responseDto = new TicketPublishAvailabilityResponseDto();
        TicketPublishAvailability ticketPublishAvailability = new TicketPublishAvailability();

        boolean countAvailability = ticketRedisRepository.checkTicketPublishCountAvailability();

        ticketPublishAvailability.setAvailability(countAvailability);
        responseDto.setData(ticketPublishAvailability);

        return responseDto;
    }
}
