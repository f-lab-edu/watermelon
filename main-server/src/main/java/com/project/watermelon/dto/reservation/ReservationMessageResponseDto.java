package com.project.watermelon.dto.reservation;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.object.ReservationMessageResponse;

public class ReservationMessageResponseDto extends CommonBackendResponseDto<ReservationMessageResponse> {
    public ReservationMessageResponseDto(ReservationMessageResponse response){
        super.setData(response);
    }
}
