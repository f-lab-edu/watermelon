package com.project.watermelon.dto.concert;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ConcertListVo;

public class ConcertListResponseDto extends CommonBackendResponseDto<ConcertListVo> {
    public ConcertListResponseDto(ConcertListVo concertList){
        super.setData(concertList);
    }
}
