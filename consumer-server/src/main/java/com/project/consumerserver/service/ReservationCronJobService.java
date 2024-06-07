package com.project.consumerserver.service;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ReservationCronJobService {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 */3 * * * *")
    public void updateReservation() {
        // 전체 전제 조건: 공연일이 현재 보다 미래인 데이터에 대해서만 수행.
        // 예매 테이블에서 AVAILABLE_AT -> 10분 지난 경우 일괄 EXPIRED 로 update
        reservationRepository.updateToExpiredStatus();

        // 예매 테이블에서 유니크한 CONCERT_MAPPING_ID 조회 (단 공연일이 지난 것들은 제외)
        List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList = reservationRepository.retrieveConcertMappingSeatCapacities();

        // 해당 CONCERT_MAPPING_ID 별 예매 상태 업데이트 플로우 시작
        // 업데이트 가능한 수 = (LOCATION 의 SEAT_CAPACITY - CONCERT_MAPPING_ID 기준 AVAILABLE, RESERVED 상태인 로우 카운트)
        for (ConcertMappingSeatInfoVO concertMappingSeatInfo : concertMappingSeatInfoList) {
            Long targetConcertMappingId = concertMappingSeatInfo.getConcertMappingId();
            Long targetSeatCapacity = concertMappingSeatInfo.getSeatCapacity();
            List<ReservationStatus> statuses = Arrays.asList(ReservationStatus.AVAILABLE, ReservationStatus.RESERVED);

            Long availableOrReservedCount = reservationRepository.retrieveAvailableOrReservedCount(targetConcertMappingId, statuses);
            Long updateCount = (targetSeatCapacity - availableOrReservedCount);

            reservationRepository.updateReservationStatus(targetConcertMappingId, updateCount);
        }
    }
}