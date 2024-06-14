package com.project.watermelon.vo;

import com.project.watermelon.enumeration.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentVo {
    private Long reservationId;
    private ReservationStatus reservationStatus;
    private Long seatId;
    private String section;
    private String rowValue;
}
