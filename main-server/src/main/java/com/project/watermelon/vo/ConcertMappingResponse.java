package com.project.watermelon.dto.object;

import com.project.watermelon.dto.concert.ConcertDto;
import com.project.watermelon.dto.concert.ConcertMappingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConcertMappingResponse {
    private List<ConcertMappingDto> concertMappingList;
}
