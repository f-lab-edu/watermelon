package com.project.watermelon.dto.concert;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ConcertMappingResponseVo;

public class ConcertMappingResponseDto extends CommonBackendResponseDto<ConcertMappingResponseVo> {
    public ConcertMappingResponseDto(ConcertMappingResponseVo concertMappingResponse){
        super.setData(concertMappingResponse);
    }
}
