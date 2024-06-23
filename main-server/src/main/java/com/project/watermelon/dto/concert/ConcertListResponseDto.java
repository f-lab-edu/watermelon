package com.project.watermelon.dto.concert;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.vo.ConcertListVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertListResponseDto extends CommonBackendResponseDto<ConcertListVo> {
    private int totalPages;
    private long totalElements;

    public ConcertListResponseDto(ConcertListVo concertList, int totalPages, long totalElements) {
        super.setData(concertList);
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
