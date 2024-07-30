package com.project.watermelon.dto.reservation;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ReservationIdVo;
import com.project.watermelon.vo.ReservationRankVo;

public class ReservationIdResponseDto extends CommonBackendResponseDto<ReservationIdVo> {
    public ReservationIdResponseDto(ReservationIdVo response){
        super.setData(response);
    }
}
