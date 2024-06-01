package com.project.watermelon.controller;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.reservation.PostReservationMessageRequestDto;
import com.project.watermelon.security.SecurityUtil;
import com.project.watermelon.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {
    private final ReservationService reservationService;

    @PostMapping("/purchase")
    public CommonBackendResponseDto<String> produceReservationMessage(@RequestBody PostReservationMessageRequestDto requestDto) {
        String email = SecurityUtil.getCurrentMemberUsername();
        return reservationService.produceReservationMessage(email, requestDto.getConcertMappingId());
    }
}
