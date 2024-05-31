package com.project.watermelon.dto.concert;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.object.ConcertListResponse;

public class ConcertListResponseDto extends CommonBackendResponseDto<ConcertListResponse> {
    public ConcertListResponseDto(ConcertListResponse concertListResponse){
        super.setData(concertListResponse);
    }
}
