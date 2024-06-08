package com.project.watermelon.service;

import com.project.watermelon.dto.concert.ConcertDto;
import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.dto.concert.ConcertMappingDto;
import com.project.watermelon.dto.concert.ConcertMappingResponseDto;
import com.project.watermelon.model.Concert;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Location;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ConcertRepository;
import com.project.watermelon.vo.ConcertListResponse;
import com.project.watermelon.vo.ConcertMappingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertMappingRepository concertMappingRepository;

    public ConcertListResponseDto retrieveConcertList() {
        // 전체 콘서트 리스트 조회
        List<Concert> concertList = concertRepository.findAll();

        List<ConcertDto> concertDtoList = new ArrayList<>();
        for (Concert concert : concertList) {
            concertDtoList.add(new ConcertDto(concert.getConcertId(), concert.getTitle(), concert.getGenre()));
        }
        ConcertListResponse concertListResponse = new ConcertListResponse(concertDtoList);
        return new ConcertListResponseDto(concertListResponse);
    }

    public ConcertMappingResponseDto retrieveConcertMapping(Long concertId) {
        // 실제 콘서트 공연 리스트 조회
        List<ConcertMapping> concertMappingList = concertMappingRepository.findByConcertIdWithDetails(concertId);

        List<ConcertMappingDto> concertMappingDtoList = new ArrayList<>();
        for (ConcertMapping concertMapping : concertMappingList) {
            Concert concert = concertMapping.getConcert();
            Location location = concertMapping.getLocation();
            concertMappingDtoList.add(
                    ConcertMappingDto.builder()
                            .concertMappingId(concertMapping.getConcertMappingId())
                            .concertId(concert.getConcertId())
                            .locationId(location.getLocationId())
                            .title(concert.getTitle())
                            .genre(concert.getGenre())
                            .concertDate(concertMapping.getConcertDate())
                            .startTime(concertMapping.getStartTime())
                            .endTime(concertMapping.getEndTime())
                            .build()
            );
        }
        ConcertMappingResponse concertMappingResponse = new ConcertMappingResponse(concertMappingDtoList);
        return new ConcertMappingResponseDto(concertMappingResponse);
    }

}
