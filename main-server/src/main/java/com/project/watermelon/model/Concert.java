package com.project.watermelon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Concert extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long concertId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String genre;

    @OneToMany(mappedBy = "concert")
    private List<ConcertMapping> concertMappingList = new ArrayList<>();
}

