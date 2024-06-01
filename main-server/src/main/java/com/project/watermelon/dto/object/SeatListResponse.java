package com.project.watermelon.dto.object;

import com.project.watermelon.dto.seat.SeatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SeatListResponse {
    private List<SeatDto> seatList;
}
