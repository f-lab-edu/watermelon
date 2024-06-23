package com.project.watermelon.service;

import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.dto.concert.ConcertMappingResponseDto;
import com.project.watermelon.model.Concert;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Location;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ConcertRepository;
import com.project.watermelon.vo.ConcertListVo;
import com.project.watermelon.vo.ConcertMappingResponseVo;
import com.project.watermelon.vo.ConcertMappingVo;
import com.project.watermelon.vo.ConcertVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertMappingRepository concertMappingRepository;

    public ConcertListResponseDto retrieveConcertList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Concert> concertPage = concertRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<ConcertVo> concertVoList = concertPage.stream()
                .map(concert -> ConcertVo.builder()
                        .concertId(concert.getConcertId())
                        .title(concert.getTitle())
                        .genre(concert.getGenre())
                        .build())
                .collect(Collectors.toList());
        ConcertListVo concertListResponse = new ConcertListVo(concertVoList);
        return new ConcertListResponseDto(concertListResponse, concertPage.getTotalPages(), concertPage.getTotalElements());
    }

    public ConcertMappingResponseDto retrieveConcertMapping(Long concertId) {
        // 실제 콘서트 공연 리스트 조회
        List<ConcertMapping> concertMappingList = concertMappingRepository.findByConcert_ConcertId(concertId);

        List<ConcertMappingVo> concertMappingVoList = concertMappingList.stream()
            .map(concertMapping -> {
                Concert concert = concertMapping.getConcert();
                Location location = concertMapping.getLocation();
                return ConcertMappingVo.builder()
                        .concertMappingId(concertMapping.getConcertMappingId())
                        .concertId(concert.getConcertId())
                        .locationId(location.getLocationId())
                        .title(concert.getTitle())
                        .genre(concert.getGenre())
                        .concertDate(concertMapping.getConcertDate())
                        .startTime(concertMapping.getStartTime())
                        .endTime(concertMapping.getEndTime())
                        .build();
            })
        .collect(Collectors.toList());
        ConcertMappingResponseVo concertMappingResponse = new ConcertMappingResponseVo(concertMappingVoList);
        return new ConcertMappingResponseDto(concertMappingResponse);
    }

}
