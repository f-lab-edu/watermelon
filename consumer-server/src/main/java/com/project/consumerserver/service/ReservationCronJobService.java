package com.project.consumerserver.service;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationCronJobService {
    private static final int PAGE_SIZE = 1000;
    private static final int EXPIRY_MINUTES = 10;
    private static final int REDIS_TTL_SECONDS = 300;

    private final ReservationRepository reservationRepository;
    private final ConcertMappingRepository concertMappingRepository;
    private final ReservationService reservationService;

    @Scheduled(cron = "0 */1 * * * *")
    public void updateReservation() {
        // expired 기준 시점 할당
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime expiryTime = currentTimestamp.minusMinutes(EXPIRY_MINUTES);

        // 아직 실행되지 않은 ConcertMapping 목록 조회
        List<Long> concertMappingIds = getFutureConcertMappingIds(currentTimestamp);
        // 10 분이 지난 status = AVAILABLE 인 Reservation 조회
        List<Reservation> reservationsToExpire = getReservationsToExpire(expiryTime, concertMappingIds);
        // reservation service 에 분산락 기반 EXPIRED 업데이트 작업 위임
        reservationService.updateReservationsToExpired(reservationsToExpire, REDIS_TTL_SECONDS);

        // 실제 공연 단위인 concertMapping 을 기준으로 reservation status AVAILABLE 업데이트 진행
        List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList = getUniqueConcertMappingSeatInfo(currentTimestamp);
        reservationService.updateReservationToAvailable(concertMappingSeatInfoList, currentTimestamp);
    }

    private List<Long> getFutureConcertMappingIds(LocalDateTime currentTimestamp) {
        return concertMappingRepository.findByConcertDateAfter(currentTimestamp).stream()
                .map(ConcertMapping::getConcertMappingId)
                .collect(Collectors.toList());
    }

    private List<Reservation> getReservationsToExpire(LocalDateTime expiryTime, List<Long> concertMappingIds) {
        Pageable expiredPageable = PageRequest.of(0, PAGE_SIZE);
        return reservationRepository.findByStatusAndAvailableAtBeforeAndConcertMappingConcertMappingIdIn(
                ReservationStatus.AVAILABLE, expiryTime, concertMappingIds, expiredPageable);
    }

    private List<ConcertMappingSeatInfoVO> getUniqueConcertMappingSeatInfo(LocalDateTime currentTimestamp) {
        List<Reservation> reservations = reservationRepository.findDistinctByConcertMappingConcertDateAfter(currentTimestamp);
        return reservations.stream()
                .map(
                        r -> new ConcertMappingSeatInfoVO(
                                r.getConcertMapping().getConcertMappingId(),
                                r.getConcertMapping().getLocation().getSeatCapacity()
                        )
                )
                .distinct()
                .toList();
    }
}
