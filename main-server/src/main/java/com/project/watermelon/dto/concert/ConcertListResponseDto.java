package com.project.watermelon.dto.concert;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ConcertListResponseVo;

public class ConcertListResponseDto extends CommonBackendResponseDto<ConcertListResponseVo> {
    public ConcertListResponseDto(ConcertListResponseVo concertListResponse){
        super.setData(concertListResponse);
    }
}
