package com.project.watermelon.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SeatDto {
    private Long seatId;
    private String section;
    private String rowValue;
    private Boolean isAvailable;
}
