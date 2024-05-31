package com.project.watermelon.controller;

import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/list")
    public ConcertListResponseDto retrieveConcertList() {
        return concertService.retrieveConcertList();
    }
}
