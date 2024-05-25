package com.project.watermelon.dto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ReservationMessageResponse {
    private String memberEmail;
    private Long waitingRank;

    public ReservationMessageResponse(String memberEmail, Long memberRank) {
        this.memberEmail = memberEmail;
        this.waitingRank = memberRank;
    }
}
