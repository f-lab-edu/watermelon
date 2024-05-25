package com.project.consumerserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReservationMessage {
    private Long concertMappingId;
    private String memberEmail;
    private Long locationId;
}
