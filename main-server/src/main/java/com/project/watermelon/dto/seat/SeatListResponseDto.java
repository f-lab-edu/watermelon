package com.project.watermelon.dto.seat;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.SeatListResponseVo;

public class SeatListResponseDto extends CommonBackendResponseDto<SeatListResponseVo> {
    public SeatListResponseDto(SeatListResponseVo seatListResponse){
        super.setData(seatListResponse);
    }
}
