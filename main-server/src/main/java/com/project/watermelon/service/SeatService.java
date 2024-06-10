package com.project.watermelon.service;

import com.project.watermelon.dto.seat.SeatListResponseDto;
import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.exception.InvalidIdException;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.repository.SeatRepository;
import com.project.watermelon.vo.ReservationSeatVo;
import com.project.watermelon.vo.SeatListResponseVo;
import com.project.watermelon.vo.SeatVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final ConcertMappingRepository concertMappingRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public SeatListResponseDto retrieveAvailableSeat(Long concertMappingId) {
        // ConcertMapping 조회
        ConcertMapping concertMapping = concertMappingRepository.findConcertMappingByConcertMappingId(concertMappingId).orElseThrow(
                () -> new InvalidIdException("Invalid concertMappingId.")
        );

        // 결제 진행 중 및 완료된 좌석 조회 -> 결제 불가한 좌석
        List<ReservationStatus> notAvailableReservationStatusList = List.of(ReservationStatus.AVAILABLE, ReservationStatus.RESERVED);
        List<Long> notAvailableSeatIdList = reservationRepository
                .findByConcertMappingIdAndStatuses(concertMappingId, notAvailableReservationStatusList)
                .stream()
                .map(
                        ReservationSeatVo::getSeatId
                ).toList();

        // 전체 좌석 조회 후 stream 으로 가용 여부 판단
        List<SeatVo> seatVoList = seatRepository.findAllByLocation_LocationId(concertMapping.getLocation().getLocationId())
                .stream()
                .map(seat -> {
                    long seatId = seat.getSeatId();
                    boolean isAvailable = !notAvailableSeatIdList.contains(seatId);
                    return  SeatVo.builder()
                            .seatId(seat.getSeatId())
                            .section(seat.getSection())
                            .rowValue(seat.getRowValue())
                            .isAvailable(isAvailable)
                            .build();
                })
                .collect(Collectors.toList());

        // SeatListResponseDto 생성 및 반환
        SeatListResponseVo seatListResponse = new SeatListResponseVo(seatVoList);
        return new SeatListResponseDto(seatListResponse);
    }
}
