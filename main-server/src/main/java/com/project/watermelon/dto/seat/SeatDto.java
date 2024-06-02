package com.project.watermelon.dto.seat;

import com.project.watermelon.enumeration.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDto {
    private Long seatId;
    private String section;
    private String rowValue;
    private Boolean isAvailable;

    @Builder
    public SeatDto(Long seatId, String section, String rowValue, Boolean isAvailable) {
        this.seatId = seatId;
        this.section = section;
        this.rowValue = rowValue;
        this.isAvailable = isAvailable;
    }
}
