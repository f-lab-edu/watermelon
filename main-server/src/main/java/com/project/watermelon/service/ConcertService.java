package com.project.watermelon.service;

import com.project.watermelon.dto.concert.ConcertDto;
import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.dto.object.ConcertListResponse;
import com.project.watermelon.model.Concert;
import com.project.watermelon.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

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

}
