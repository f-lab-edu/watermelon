package com.project.watermelon.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConcertMappingResponseVo {
    private List<ConcertMappingVo> concertMappingList;
}
