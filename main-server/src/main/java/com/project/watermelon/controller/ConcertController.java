package com.project.watermelon.controller;

import com.project.watermelon.dto.concert.ConcertListResponseDto;
import com.project.watermelon.dto.concert.ConcertMappingResponseDto;
import com.project.watermelon.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/list")
    public ConcertListResponseDto retrieveConcertList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return concertService.retrieveConcertList(page, size);
    }

    @GetMapping("/mapping/{concertId}")
    public ConcertMappingResponseDto retrieveConcertMapping(@PathVariable("concertId") Long concertId) {
        return concertService.retrieveConcertMapping(concertId);
    }
}
