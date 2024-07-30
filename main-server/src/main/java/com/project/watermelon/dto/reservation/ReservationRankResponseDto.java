package com.project.watermelon.dto.reservation;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ReservationMessageVo;
import com.project.watermelon.vo.ReservationRankVo;

public class ReservationRankResponseDto extends CommonBackendResponseDto<ReservationRankVo> {
    public ReservationRankResponseDto(ReservationRankVo response){
        super.setData(response);
    }
}
