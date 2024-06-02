package com.project.watermelon.controller;

import com.project.watermelon.dto.seat.SeatListResponseDto;
import com.project.watermelon.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/available/{concertMappingId}")
    public SeatListResponseDto retrieveAvailableSeat(@PathVariable("concertMappingId") Long concertMappingId) {
        return seatService.retrieveAvailableSeat(concertMappingId);
    }
}
