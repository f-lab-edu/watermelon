package com.project.watermelon.dto.reservation;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ReservationMessageVo;

public class ReservationMessageResponseDto extends CommonBackendResponseDto<ReservationMessageVo> {
    public ReservationMessageResponseDto(ReservationMessageVo response){
        super.setData(response);
    }
}
