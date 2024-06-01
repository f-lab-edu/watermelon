package com.project.watermelon.dto.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ConcertMappingDto {
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
