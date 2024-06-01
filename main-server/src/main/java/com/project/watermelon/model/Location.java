package com.project.watermelon.model;

import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Location extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long locationId;

    @Column(nullable = false)
    private String locationName;

    @Column()
    private Long seatCapacity;

//    @OneToMany(mappedBy = "location")
//    private List<ConcertMapping> concertMappingList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "location")
//    private List<Seat> seatList = new ArrayList<>();
}
