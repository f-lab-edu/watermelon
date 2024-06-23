package com.project.consumerserver.service;

import com.project.consumerserver.dto.ReservationMessage;
import com.project.consumerserver.repository.ReservationRedisRepository;
import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Member;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.MemberRepository;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRedisRepository reservationRedisRepository;
    private final ConcertMappingRepository concertMappingRepository;

    @Transactional
    public void updateReservationsToExpired(List<Reservation> reservationsToExpire) {
        reservationsToExpire.forEach(Reservation::updateReservationStatusExpire);
        reservationRepository.saveAll(reservationsToExpire);
    }

    @Transactional
    public void updateReservationStatus(List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList, LocalDateTime currentTimestamp) {
        for (ConcertMappingSeatInfoVO concertMappingSeatInfo : concertMappingSeatInfoList) {
            Long targetConcertMappingId = concertMappingSeatInfo.getConcertMappingId();
            Long targetSeatCapacity = concertMappingSeatInfo.getSeatCapacity();
            List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.AVAILABLE, ReservationStatus.RESERVED);

            Long availableOrReservedCount = reservationRepository.countByStatusInAndConcertMappingConcertMappingId(
                    statuses, targetConcertMappingId);
            long updateCount = (targetSeatCapacity - availableOrReservedCount);
            int updateCountInt = Math.toIntExact(updateCount);

            Pageable availablePageable = PageRequest.of(0, updateCountInt);
            List<Reservation> reservationsToAvailable = reservationRepository.findByConcertMappingConcertMappingIdAndStatusOrderByReservationRank(
                    targetConcertMappingId, ReservationStatus.WAIT, availablePageable);
            reservationsToAvailable.forEach(reservation -> reservation.updateReservationStatusAvailable(currentTimestamp));
            reservationRepository.saveAll(reservationsToAvailable);
        }
    }

    @Transactional
    public boolean processMessage(ReservationMessage message) {
        try {
            long concertMappingId = message.getConcertMappingId();
            String stringConcertMappingId = Long.toString(concertMappingId);
            ConcertMapping concertMapping = concertMappingRepository.findConcertMappingByConcertMappingId(concertMappingId).orElseThrow(
                    () -> new IllegalArgumentException("invalid concertMappingId: " + stringConcertMappingId)
            );

            Member member = memberRepository.findByEmail(message.getMemberEmail()).orElseThrow(
                    () -> new IllegalArgumentException("Invalid email: " + message.getMemberEmail())
            );
            // 순번 지정해주는 로직 추가
            // 현재 예매 순번 조회 (atomic increment by)
            Long reservationRank = reservationRedisRepository.incrementReservationRankAndReturn(stringConcertMappingId);
            // 예매 순번 정보 insert
            reservationRepository.save(
                    Reservation
                            .builder()
                            .reservationRank(reservationRank)
                            .status(ReservationStatus.WAIT)
                            .concertMapping(concertMapping)
                            .member(member)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
