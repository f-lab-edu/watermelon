package com.project.watermelon.dto.seat;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.object.SeatListResponse;

public class SeatListResponseDto extends CommonBackendResponseDto<SeatListResponse> {
    public SeatListResponseDto(SeatListResponse seatListResponse){
        super.setData(seatListResponse);
    }
}