package com.project.watermelon.vo;

import com.project.watermelon.enumeration.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReservationRankVo {
    private int reservationRank;
    private ReservationStatus reservationStatus;
}
