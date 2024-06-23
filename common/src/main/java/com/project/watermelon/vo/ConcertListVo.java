package com.project.watermelon.vo;

import com.project.watermelon.vo.ConcertVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConcertListVo {
    private List<ConcertVo> concertList;
}
