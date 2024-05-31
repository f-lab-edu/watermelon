package com.project.watermelon.dto.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConcertDto {
    private Long concertId;
    private String title;
    private String genre;
}
