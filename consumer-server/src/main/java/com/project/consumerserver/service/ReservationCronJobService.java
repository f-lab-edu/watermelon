package com.project.consumerserver.service;

import com.project.consumerserver.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class ReservationCronJobService {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 */3 * * * *")
    public void updateReservation() {
        // 예메 테이블에서 유니크한 CONCERT_MAPPING_ID 조회
        // 해당 CONCERT_MAPPING_ID 별 예매 상태 업데이트 플로우 시작
        // 업데이트 가능한 수 = (LOCATION 의 SEAT_CAPACITY - CONCERT_MAPPING_ID 기준 AVAILABLE, RESERVED 상태인 로우 카운트)
        // 위와 같은 정책을 바탕으로 업데이트 진행

    }
}
