package com.project.consumerserver.service;

import com.project.consumerserver.dto.ConcertMappingSeatInfoDTO;
import com.project.consumerserver.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@RequiredArgsConstructor
public class ReservationCronJobService {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 */3 * * * *")
    public void updateReservation() {
        // 예메 테이블에서 유니크한 CONCERT_MAPPING_ID 조회
        List<ConcertMappingSeatInfoDTO> concertMappingSeatInfoList = reservationRepository.retrieveConcertMappingSeatCapacities();

        // 해당 CONCERT_MAPPING_ID 별 예매 상태 업데이트 플로우 시작
        // 업데이트 가능한 수 = (LOCATION 의 SEAT_CAPACITY - CONCERT_MAPPING_ID 기준 AVAILABLE, RESERVED 상태인 로우 카운트)
        for (ConcertMappingSeatInfoDTO concertMappingSeatInfo : concertMappingSeatInfoList) {
            Long targetConcertMappingId = concertMappingSeatInfo.getConcertMappingId();
            Long targetSeatCapacity = concertMappingSeatInfo.getSeatCapacity();
            Long availableOrReservedCount = reservationRepository.retrieveAvailableOrReservedCount(targetConcertMappingId);
            Long updateCount = (targetSeatCapacity - availableOrReservedCount);

            reservationRepository.updateReservationStatus(targetConcertMappingId, updateCount);
        }
    }
}