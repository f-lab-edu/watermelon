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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRedisRepository reservationRedisRepository;
    private final ConcertMappingRepository concertMappingRepository;

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
