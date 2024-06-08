package com.project.watermelon.dto.object;

import com.project.watermelon.dto.concert.ConcertDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConcertListResponse {
    private List<ConcertDto> concertList;
}
