package com.project.watermelon.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ConcertMappingVo {
    private Long concertMappingId;
    private Long concertId;
    private Long locationId;

    private String title;
    private String genre;
    private LocalDateTime concertDate;
    private String startTime;
    private String endTime;
    private String locationName;
}
