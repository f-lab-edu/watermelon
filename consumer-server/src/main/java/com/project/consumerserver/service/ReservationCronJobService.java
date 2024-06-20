package com.project.consumerserver.service;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationCronJobService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ReservationRepository reservationRepository;
    private final ConcertMappingRepository concertMappingRepository;

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void updateReservation() {
        // 전체 전제 조건: 공연일이 현재 보다 미래인 데이터에 대해서만 수행.
        // 예매 테이블에서 AVAILABLE_AT -> 10분 지난 경우 일괄 EXPIRED 로 update
        // 결제 시 reservation lock check -> 분산락 키워드 검색해보기!
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime expiryTime = currentTimestamp.minusMinutes(10);

        // 락이 걸린 예약 ID 리스트 조회
        String lockedReservationListKey = "lockedReservationList";
        Set<String> lockedReservationIdSet = Optional.ofNullable(stringRedisTemplate.opsForSet().members(lockedReservationListKey)).orElse(Collections.emptySet());
        Set<Long> lockedReservationIds = lockedReservationIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());

        // 만료될 예약 데이터 조회
        List<Long> concertMappingIds = concertMappingRepository.findByConcertDateAfter(currentTimestamp).stream()
                .map(ConcertMapping::getConcertMappingId)
                .collect(Collectors.toList());

        // pageable 을 통해 1000개 씩 조회
        Pageable expiredPageable = PageRequest.of(0, 1000);
        List<Reservation> reservationToExpire = reservationRepository.findByStatusAndAvailableAtBeforeAndConcertMappingConcertMappingIdIn(
                ReservationStatus.AVAILABLE, expiryTime, concertMappingIds, expiredPageable);
        // lockedReservationIds 에 해당하는 예약 데이터 제거
        List<Reservation> filteredReservationsToExpire = reservationToExpire.stream()
                .filter(reservation -> !lockedReservationIds.contains(reservation.getReservationId()))
                .toList();

        // 예약 EXPIRED 상태로 업데이트
        filteredReservationsToExpire.forEach(Reservation::updateReservationStatusExpire);
        reservationRepository.saveAll(filteredReservationsToExpire);

        // 예매 테이블에서 유니크한 CONCERT_MAPPING_ID 조회 (단 공연일이 지난 것들은 제외)
        List<Reservation> reservations = reservationRepository.findDistinctByConcertMappingConcertDateAfter(currentTimestamp);
        List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList = reservations.stream()
        .map(r -> new ConcertMappingSeatInfoVO(r.getConcertMapping().getConcertMappingId(), r.getConcertMapping().getLocation().getSeatCapacity()))
        .distinct()
        .toList();

        // 해당 CONCERT_MAPPING_ID 별 예매 상태 업데이트 플로우 시작
        // 업데이트 가능한 수 = (LOCATION 의 SEAT_CAPACITY - CONCERT_MAPPING_ID 기준 AVAILABLE, RESERVED 상태인 로우 카운트)
        for (ConcertMappingSeatInfoVO concertMappingSeatInfo : concertMappingSeatInfoList) {
            Long targetConcertMappingId = concertMappingSeatInfo.getConcertMappingId();
            Long targetSeatCapacity = concertMappingSeatInfo.getSeatCapacity();
            List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.AVAILABLE, ReservationStatus.RESERVED);

            Long availableOrReservedCount = reservationRepository.countByStatusInAndConcertMappingConcertMappingId(
                    statuses, targetConcertMappingId);
            long updateCount = (targetSeatCapacity - availableOrReservedCount);
            int updateCountInt = Math.toIntExact(updateCount);

            // 예약 데이터를 페이징을 사용하여 조회
            Pageable availablePageable = PageRequest.of(0, updateCountInt);
            List<Reservation> reservationsToAvailable = reservationRepository.findByConcertMappingConcertMappingIdAndStatusOrderByReservationRank(
                    targetConcertMappingId, ReservationStatus.WAIT, availablePageable);
            // AVAILABLE status 로 업데이트
            reservationsToAvailable.forEach(reservation -> reservation.updateReservationStatusAvailable(currentTimestamp));
            reservationRepository.saveAll(reservationsToAvailable);
        }
    }
}