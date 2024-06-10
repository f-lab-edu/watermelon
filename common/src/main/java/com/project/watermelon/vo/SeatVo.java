package com.project.watermelon.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatVo {
    private Long seatId;
    private String section;
    private String rowValue;
    private Boolean isAvailable;

    @Builder
    public SeatVo(Long seatId, String section, String rowValue, Boolean isAvailable) {
        this.seatId = seatId;
        this.section = section;
        this.rowValue = rowValue;
        this.isAvailable = isAvailable;
    }
}
