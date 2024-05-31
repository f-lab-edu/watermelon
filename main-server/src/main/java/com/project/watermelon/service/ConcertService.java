package com.project.watermelon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.concert.ConcertDto;
import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.dto.object.ConcertListResponse;
import com.project.watermelon.exception.MemberAlreadyRequestReservationException;
import com.project.watermelon.model.Concert;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ConcertRepository;
import com.project.watermelon.repository.ReservationRedisRepository;
import com.project.watermelon.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
