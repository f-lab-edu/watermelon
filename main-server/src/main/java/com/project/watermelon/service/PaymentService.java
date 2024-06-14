package com.project.watermelon.service;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.payment.PaymentResponseDto;
import com.project.watermelon.dto.seat.SeatListResponseDto;
import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ReservationRedisRepository;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.repository.SeatRepository;
import com.project.watermelon.vo.PaymentVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ConcertMappingRepository concertMappingRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRedisRepository reservationRedisRepository;

    public void lockReservation(Long reservationId) {

        String lockedReservationListKey = "lockedReservationList";

        // reservation lock 생성 (TTL: 10 minutes)
        reservationRedisRepository.lockReservation(reservationId, 600);

        // Add the reservation ID to the set of locked reservations
        reservationRedisRepository.addLockedReservationId(lockedReservationListKey, reservationId);

    }

    public PaymentResponseDto processPayment(String email, Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId).orElseThrow(
                () -> new IllegalArgumentException("Invalid reservationId: " + reservationId)
        );



        // dummy data return
        PaymentVo paymentVo = new PaymentVo(1L, ReservationStatus.RESERVED, 1L, "A", "99");
        return new PaymentResponseDto(paymentVo);
    }
}
