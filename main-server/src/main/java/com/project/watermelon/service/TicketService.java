package com.project.watermelon.service;

import com.project.watermelon.dto.concert.ConcertDto;
import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.dto.concert.ConcertMappingDto;
import com.project.watermelon.dto.concert.ConcertMappingResponseDto;
import com.project.watermelon.dto.object.ConcertListResponse;
import com.project.watermelon.dto.object.ConcertMappingResponse;
import com.project.watermelon.model.Concert;
import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Location;
import com.project.watermelon.repository.ConcertMappingRepository;
import com.project.watermelon.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final ConcertRepository concertRepository;
    private final ConcertMappingRepository concertMappingRepository;
}
