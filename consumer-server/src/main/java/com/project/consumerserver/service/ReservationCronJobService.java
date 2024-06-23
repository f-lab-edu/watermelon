package com.project.consumerserver.service;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ConcertMappingRepository;
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

    private static final String LOCKED_RESERVATION_LIST_KEY = "lockedReservationList";
    private static final int PAGE_SIZE = 1000;
    private static final int EXPIRY_MINUTES = 10;

    private final StringRedisTemplate stringRedisTemplate;
    private final ReservationRepository reservationRepository;
    private final ConcertMappingRepository concertMappingRepository;
    private final ReservationService reservationService;

    @Scheduled(cron = "0 */1 * * * *")
    public void updateReservation() {
        // expired 기준 시점 할당
        LocalDateTime currentTimestamp = LocalDateTime.now();
        LocalDateTime expiryTime = currentTimestamp.minusMinutes(EXPIRY_MINUTES);

        // reservation lock 이 걸려있는 Reservation 목록 id 조회
        Set<Long> lockedReservationIds = getLockedReservationIds();
        // 아직 실행되지 않은 ConcertMapping 목록 조회
        List<Long> concertMappingIds = getFutureConcertMappingIds(currentTimestamp);

        // reservation lock 이 걸려있지 않은 Reservation 중 조건 충족하는 객체 상태 EXPIRED 로 업데이트
        List<Reservation> filteredReservationsToExpire = getFilteredReservationsToExpire(expiryTime, concertMappingIds, lockedReservationIds);
        reservationService.updateReservationsToExpired(filteredReservationsToExpire);

        List<ConcertMappingSeatInfoVO> concertMappingSeatInfoList = getUniqueConcertMappingSeatInfo(currentTimestamp);
        reservationService.updateReservationStatus(concertMappingSeatInfoList, currentTimestamp);
    }

    private Set<Long> getLockedReservationIds() {
        Set<String> lockedReservationIdSet = Optional.ofNullable(stringRedisTemplate.opsForSet().members(LOCKED_RESERVATION_LIST_KEY)).orElse(Collections.emptySet());
        return lockedReservationIdSet.stream().map(Long::valueOf).collect(Collectors.toSet());
    }

    private List<Long> getFutureConcertMappingIds(LocalDateTime currentTimestamp) {
        return concertMappingRepository.findByConcertDateAfter(currentTimestamp).stream()
                .map(ConcertMapping::getConcertMappingId)
                .collect(Collectors.toList());
    }

    private List<Reservation> getFilteredReservationsToExpire(LocalDateTime expiryTime, List<Long> concertMappingIds, Set<Long> lockedReservationIds) {
        Pageable expiredPageable = PageRequest.of(0, PAGE_SIZE);
        List<Reservation> reservationToExpire = reservationRepository.findByStatusAndAvailableAtBeforeAndConcertMappingConcertMappingIdIn(
                ReservationStatus.AVAILABLE, expiryTime, concertMappingIds, expiredPageable);
        return reservationToExpire.stream()
                .filter(reservation -> !lockedReservationIds.contains(reservation.getReservationId()))
                .toList();
    }

    private List<ConcertMappingSeatInfoVO> getUniqueConcertMappingSeatInfo(LocalDateTime currentTimestamp) {
        List<Reservation> reservations = reservationRepository.findDistinctByConcertMappingConcertDateAfter(currentTimestamp);
        return reservations.stream()
                .map(r -> new ConcertMappingSeatInfoVO(r.getConcertMapping().getConcertMappingId(), r.getConcertMapping().getLocation().getSeatCapacity()))
                .distinct()
                .toList();
    }
}
