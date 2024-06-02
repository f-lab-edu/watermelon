package com.project.watermelon.dto.concert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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

    @Builder
    public ConcertMappingDto(
            Long concertMappingId,
            Long concertId,
            Long locationId,
            String title,
            String genre,
            LocalDateTime concertDate,
            String startTime,
            String endTime,
            String locationName
    ) {
        this.concertMappingId = concertMappingId;
        this.concertId = concertId;
        this.locationId = locationId;
        this.title = title;
        this.genre = genre;
        this.concertDate = concertDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationName = locationName;
    }
}
