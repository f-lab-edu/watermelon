package com.project.watermelon.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatVo {
    private Long seatId;
    private String section;
    private String rowValue;
    private Boolean isAvailable;
}
