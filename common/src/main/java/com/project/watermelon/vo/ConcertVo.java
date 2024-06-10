package com.project.watermelon.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConcertVo {
    private Long concertId;
    private String title;
    private String genre;

    @Builder
    public ConcertVo(
            Long concertId,
            String title,
            String genre
    ) {
        this.concertId = concertId;
        this.title = title;
        this.genre = genre;
    }
}
