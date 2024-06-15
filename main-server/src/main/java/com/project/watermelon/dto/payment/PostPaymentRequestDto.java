package com.project.watermelon.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostPaymentRequestDto {
    private Long reservationId;
    private Long seatId;
}
