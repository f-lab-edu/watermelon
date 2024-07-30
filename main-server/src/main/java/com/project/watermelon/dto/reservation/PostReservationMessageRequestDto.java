package com.project.watermelon.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class PostReservationMessageRequestDto {
//    private String email;
    private Long concertMappingId;
}
