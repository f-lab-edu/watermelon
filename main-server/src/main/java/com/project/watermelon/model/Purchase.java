package com.project.watermelon.model;

import com.project.watermelon.enumeration.MemberRole;
import com.project.watermelon.enumeration.PurchaseStatus;
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
public class Purchase extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long purchaseId;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private Long price;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PurchaseStatus purchaseStatus = PurchaseStatus.PROGRESS;

    @OneToOne
    @JoinColumn(name="ticketId", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;
}

