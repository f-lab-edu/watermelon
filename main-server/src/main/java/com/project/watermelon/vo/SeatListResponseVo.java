package com.project.watermelon.vo;

import com.project.watermelon.dto.seat.SeatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SeatListResponseVo {
    private List<SeatDto> seatList;
}
