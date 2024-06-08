package com.project.watermelon.enumeration;

public enum ReservationStatus {
    WAIT, // 대기 상태
    AVAILABLE, // 예약 가능 상태
    RESERVED, // 예약된 상태
    EXPIRED, // AVAILABLE 만료된 상태
    CANCELLED // 예매 결제가 취소된 상태
}
