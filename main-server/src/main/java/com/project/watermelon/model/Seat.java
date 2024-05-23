package com.project.watermelon.model;

import com.project.watermelon.enumeration.SeatStatus;
import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Seat extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long seatId;

    @Column()
    private String section;

    @Column()
    private String rowValue;

    @Column()
    private String number;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "locationId", nullable = false)
    private Location location;
}

