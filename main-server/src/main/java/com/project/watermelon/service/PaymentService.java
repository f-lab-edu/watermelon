package com.project.watermelon.service;

import com.project.watermelon.annotation.RedisLock;
import com.project.watermelon.dto.payment.PaymentResponseDto;
import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.enumeration.SeatStatus;
import com.project.watermelon.exception.InvalidIdException;
import com.project.watermelon.exception.MemberAlreadyRequestReservationException;
import com.project.watermelon.exception.NotAvailableStatusException;
import com.project.watermelon.exception.SeatAlreadyReservedException;
import com.project.watermelon.model.LockKey;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.model.Seat;
import com.project.watermelon.model.Ticket;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.repository.SeatRepository;
import com.project.watermelon.repository.TicketRepository;
import com.project.watermelon.vo.PaymentVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final TicketRepository ticketRepository;
    private final SeatService seatService;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @RedisLock(keyType = LockKey.class, lockPrefix = "reservationLock:") // 결제 중 EXPIRED 상태가 되는 것을 방지
    @Transactional
    public PaymentResponseDto processPayment(String email, LockKey reservationLockKey, Long seatId) {
        Long reservationId = reservationLockKey.getLockKey();
        try {
            // 멤버 이메일에 해당 reservationId가 유효한지 판단
            Reservation reservation = reservationRepository.findByMember_EmailAndReservationId(email, reservationId).orElseThrow(
                    () -> new MemberAlreadyRequestReservationException("The member is not the owner of this reservation: " + email)
            );

            // AVAILABLE status 가 아닌 경우 예외 처리
            if (reservation.getStatus() != ReservationStatus.AVAILABLE) {
                throw new NotAvailableStatusException("Not available status.");
            }

            // seat 조회 및 락 처리
            LockKey seatLockKey = new LockKey(seatId);
            Seat seat = seatService.lockAndRetrieveSeat(seatLockKey, seatId);

            // seat 이 이미 예약된 좌석인지 판단
            if (isSeatReserved(seatId)) {
                throw new SeatAlreadyReservedException("Seat already reserved: " + seatId);
            }

            // ticket 생성 및 reservation status 업데이트, seat status 업데이트
            Ticket ticket = Ticket.builder().concertMapping(reservation.getConcertMapping()).seat(seat).build();
            ticketRepository.save(ticket);
            reservation.setStatus(ReservationStatus.RESERVED);// Update the reservation status directly
            reservation.setTicket(ticket);// assign ticket
            reservationRepository.save(reservation); // Persist the change
            seat.setStatus(SeatStatus.RESERVED);
            seatRepository.save(seat);

            // 예매 (결제) 성공 시 해당 내역 return
            PaymentVo paymentVo = new PaymentVo(reservationId, ReservationStatus.RESERVED, seatId, seat.getSection(), seat.getRowValue());
            return new PaymentResponseDto(paymentVo);
        } catch (Exception e) {
            logger.error("Payment processing failed", e);
            return createFailedPaymentResponse("Payment processing failed: " + e.getMessage());
        }
    }

    public boolean isSeatReserved(Long seatId) {
        return ticketRepository.existsBySeat_SeatId(seatId);
    }

    private PaymentResponseDto createFailedPaymentResponse(String message) {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.markAsFailed(message);
        return paymentResponseDto;
    }
}
