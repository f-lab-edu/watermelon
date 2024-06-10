package com.project.watermelon.vo;

import com.project.watermelon.dto.concert.ConcertDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConcertListResponseVo {
    private List<ConcertDto> concertList;
}
