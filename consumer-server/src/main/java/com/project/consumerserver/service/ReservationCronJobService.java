package com.project.consumerserver.service;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
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

    @Scheduled(cron = "0 */1 * * * *")
    public void updateReservation() {
        // 전체 전제 조건: 공연일이 현재 보다 미래인 데이터에 대해서만 수행.
        // 예매 테이블에서 AVAILABLE_AT -> 10분 지난 경우 일괄 EXPIRED 로 update
        // 결제 시 reservation lock check -> 분산락 키워드 검색해보기!
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime expiryTime = currentTimestamp.minusMinutes(10);

        // 락이 걸린 예약 ID 리스트 조회
        String lockedReservationListKey = "lockedReservationList";
        Set<String> lockedReservationIdSet = Optional.ofNullable(stringRedisTemplate.opsForSet().members(lockedReservationListKey)).orElse(Collections.emptySet());
        List<Long> lockedReservationIds = lockedReservationIdSet.stream().map(Long::valueOf).collect(Collectors.toList());

        // 예매 상태를 EXPIRED로 업데이트 (락이 걸린 예약 ID들은 제외)
        // 추후 lockedReservationIds 사이즈가 커지는 경우 -> 반복문으로 끊기
        reservationRepository.updateToExpiredStatus(currentTimestamp, expiryTime, lockedReservationIds);

        // 예매 테이블에서 유니크한 CONCERT_MAPPING_ID 조회 (단 공연일이 지난 것들은 제외)
        List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList = reservationRepository.retrieveConcertMappingSeatCapacities(currentTimestamp);

        // 해당 CONCERT_MAPPING_ID 별 예매 상태 업데이트 플로우 시작
        // 업데이트 가능한 수 = (LOCATION 의 SEAT_CAPACITY - CONCERT_MAPPING_ID 기준 AVAILABLE, RESERVED 상태인 로우 카운트)
        for (ConcertMappingSeatInfoVO concertMappingSeatInfo : concertMappingSeatInfoList) {
            Long targetConcertMappingId = concertMappingSeatInfo.getConcertMappingId();
            Long targetSeatCapacity = concertMappingSeatInfo.getSeatCapacity();
            List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.AVAILABLE, ReservationStatus.RESERVED);

            Long availableOrReservedCount = reservationRepository.retrieveAvailableOrReservedCount(targetConcertMappingId, statuses);
            long updateCount = (targetSeatCapacity - availableOrReservedCount);
            int updateCountInt = Math.toIntExact(updateCount);

            // 1. 예약 ID를 페이징을 사용하여 가져옴
            Pageable pageable = PageRequest.of(0, updateCountInt);
            List<Long> reservationIdList = reservationRepository.findReservationIdsForUpdate(targetConcertMappingId, pageable);

            // 2. 가져온 예약 ID를 사용하여 상태 업데이트
            if (!reservationIdList.isEmpty()) {
                reservationRepository.updateReservationStatus(reservationIdList, currentTimestamp);
            }
        }
    }
}