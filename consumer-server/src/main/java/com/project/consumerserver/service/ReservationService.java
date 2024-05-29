package com.project.consumerserver.service;

import com.project.consumerserver.dto.ReservationMessage;
import com.project.consumerserver.enumeration.ReservationStatus;
import com.project.consumerserver.repository.MemberRepository;
import com.project.consumerserver.repository.ReservationRedisRepository;
import com.project.consumerserver.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRedisRepository reservationRedisRepository;

    @Transactional
    public boolean processMessage(ReservationMessage message) {
        try {
            long concertMappingId = message.getConcertMappingId();
            String stringConcertMappingId = Long.toString(concertMappingId);
            Long memberId = memberRepository.retrieveMemberIdByEmail(message.getMemberEmail());
            // 순번 지정해주는 로직 추가
            // 현재 예매 순번 조회 (atomic increment by)
            Long reservationRank = reservationRedisRepository.incrementReservationRankAndReturn(stringConcertMappingId);
            // 예매 순번 정보 insert
            reservationRepository.createReservation(concertMappingId, memberId, reservationRank, ReservationStatus.WAIT.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
