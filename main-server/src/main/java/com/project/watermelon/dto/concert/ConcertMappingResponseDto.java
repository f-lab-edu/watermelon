package com.project.watermelon.dto.concert;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.object.ConcertListResponse;
import com.project.watermelon.dto.object.ConcertMappingResponse;

public class ConcertMappingResponseDto extends CommonBackendResponseDto<ConcertMappingResponse> {
    public ConcertMappingResponseDto(ConcertMappingResponse concertMappingResponse){
        super.setData(concertMappingResponse);
    }
}
