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
import com.project.watermelon.service.RedisLockService;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRedisRepository reservationRedisRepository;
    private final ConcertMappingRepository concertMappingRepository;
    private final RedisLockService redisLockService;


    @Transactional
    public void updateReservationsToExpired(List<Reservation> reservationsToExpire, int redisTtlSeconds) {
        List<String> lockKeys = new ArrayList<>();
        List<Reservation> filteredReservationsToExpire = new ArrayList<>();

        try {
            for (Reservation reservation : reservationsToExpire) {
                String lockKey = getLockKey(reservation.getReservationId());
                // 락 획득
                if (redisLockService.lock(lockKey, redisTtlSeconds)) {
                    filteredReservationsToExpire.add(reservation);
                    lockKeys.add(lockKey);
                }
            }
            filteredReservationsToExpire.forEach(Reservation::updateReservationStatusExpire);
            reservationRepository.saveAll(filteredReservationsToExpire);
        } finally {
            // 락 해제
            lockKeys.forEach(redisLockService::unlock);
        }
    }

    @Transactional
    public void updateReservationToAvailable(List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList, LocalDateTime currentTimestamp) {
        // 실제 공연 단위인 concertMapping 을 기준으로 순회
        for (ConcertMappingSeatInfoVO concertMappingSeatInfo : concertMappingSeatInfoList) {
            Long targetConcertMappingId = concertMappingSeatInfo.getConcertMappingId();
            Long targetSeatCapacity = concertMappingSeatInfo.getSeatCapacity();
            List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.AVAILABLE, ReservationStatus.RESERVED);

            // status AVAILABLE, RESERVED 인 reservation 카운트
            Long availableOrReservedCount = reservationRepository.countByStatusInAndConcertMappingConcertMappingId(
                    statuses, targetConcertMappingId);
            // update 가능 카운트 = 전체 좌석 수 - AVAILABLE or RESERVED 좌석 수
            long updateCount = (targetSeatCapacity - availableOrReservedCount);
            int updateCountInt = Math.toIntExact(updateCount);

            if (updateCountInt <= 0) {
                return;
            }

            // update 가능 카운트 만큼 WAIT -> AVAILABLE status 로 업데이트
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

    private String getLockKey(Long reservationId) {
        // 추가적인 더하기 연산이 발생할 경우 StringBuilder 를 사용하여 메모리 효율성 개선
        return "reservationLock:" + reservationId;
    }
}
