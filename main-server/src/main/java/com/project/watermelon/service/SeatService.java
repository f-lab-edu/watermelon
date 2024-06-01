package com.project.watermelon.service;

import com.project.watermelon.dto.object.SeatListResponse;
import com.project.watermelon.dto.seat.SeatDto;
import com.project.watermelon.dto.seat.SeatListResponseDto;
import com.project.watermelon.enumeration.PurchaseStatus;
import com.project.watermelon.exception.InvalidIdException;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Purchase;
import com.project.watermelon.model.Seat;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.PurchaseRepository;
import com.project.watermelon.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final ConcertMappingRepository concertMappingRepository;
    private final SeatRepository seatRepository;
    private final PurchaseRepository purchaseRepository;

    public SeatListResponseDto retrieveAvailableSeat(Long concertMappingId) {
        // ConcertMapping 조회
        ConcertMapping concertMapping = concertMappingRepository.findByConcertMappingId(concertMappingId).orElseThrow(
                () -> new InvalidIdException("Invalid concertMappingId.")
        );

        // 전체 좌석 정보 조회
        List<Seat> allSeatList = seatRepository.findAllByLocation_LocationId(concertMapping.getLocation().getLocationId());

        // 결제 진행 중 및 완료된 좌석 조회 -> 결제 불가한 좌석
        List<PurchaseStatus> notAvailablePurchaseStatusList = List.of(PurchaseStatus.PROGRESS, PurchaseStatus.DONE);
        List<Purchase> notAvailablePurchaseList = purchaseRepository.findByConcertMappingIdAndStatuses(concertMappingId, notAvailablePurchaseStatusList);

        List<SeatDto> seatDtoList = new ArrayList<>();
        List<Long> notAvailableSeatIdList = new ArrayList<>();

        // notAvailablePurchaseList 에서 좌석 ID 추출
        for (Purchase purchase : notAvailablePurchaseList) {
            notAvailableSeatIdList.add(purchase.getTicket().getSeat().getSeatId());
        }

        // allSeatList 를 순회하며 사용 가능 여부를 판단하여 SeatDto 생성
        for (Seat seat : allSeatList) {
            long seatId = seat.getSeatId();
            boolean isAvailable = !notAvailableSeatIdList.contains(seatId);
            seatDtoList.add(new SeatDto(seat.getSeatId(), seat.getSection(), seat.getRowValue(), isAvailable));
        }

        // SeatListResponseDto 생성 및 반환
        SeatListResponse seatListResponse = new SeatListResponse(seatDtoList);
        return new SeatListResponseDto(seatListResponse);
    }
}
