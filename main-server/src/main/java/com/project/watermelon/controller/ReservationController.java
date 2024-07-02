package com.project.watermelon.controller;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.reservation.PostReservationMessageRequestDto;
import com.project.watermelon.dto.reservation.ReservationMessageResponseDto;
import com.project.watermelon.dto.reservation.ReservationRankResponseDto;
import com.project.watermelon.security.SecurityUtil;
import com.project.watermelon.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/message")
    public CommonBackendResponseDto<String> produceReservationMessage(@RequestBody PostReservationMessageRequestDto requestDto) {
        String email = SecurityUtil.getCurrentMemberUsername();
        return reservationService.produceReservationMessage(email, requestDto.getConcertMappingId());
    }

    @GetMapping("/rank/{concertMappingId}/{reservationId}")
    public ReservationRankResponseDto getReservationRank(@PathVariable("concertMappingId") Long concertMappingId, @PathVariable("reservationId") Long reservationId) {
        return reservationService.retrieveReservationRank(concertMappingId, reservationId);
    }
}
