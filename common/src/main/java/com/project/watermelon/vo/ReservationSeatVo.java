package com.project.watermelon.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class ReservationSeatVo {
    private Long reservationId;
    private Long ticketId;
    private Long seatId;
}
