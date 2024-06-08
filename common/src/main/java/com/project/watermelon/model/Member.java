package com.project.watermelon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import com.project.watermelon.enumeration.MemberRole;
import com.project.watermelon.util.Timestamped;


@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long memberId;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole = MemberRole.MEMBER;

//    @OneToMany(mappedBy = "member")
//    private List<Purchase> purchaseList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Reservation> reservationList = new ArrayList<>();

    @Builder
    public Member(String memberName, String password, MemberRole memberRole, String email) {
        this.memberName = memberName;
        this.password = password;
        this.memberRole = memberRole;
        this.email = email;
    }
}
