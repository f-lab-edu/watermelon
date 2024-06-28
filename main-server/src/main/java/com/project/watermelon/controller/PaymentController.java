package com.project.watermelon.controller;

import com.project.watermelon.dto.payment.PaymentResponseDto;
import com.project.watermelon.dto.payment.PostPaymentRequestDto;
import com.project.watermelon.security.SecurityUtil;
import com.project.watermelon.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    public PaymentResponseDto processPayment(@RequestBody PostPaymentRequestDto requestDto) {
        String email = SecurityUtil.getCurrentMemberUsername();
        return paymentService.processPayment(email, requestDto.getReservationId(), requestDto.getSeatId());
    }
}
