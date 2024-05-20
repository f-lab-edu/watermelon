package com.project.watermelon.repository;


import com.project.watermelon.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByMemberName(String memberName);

    boolean existsByMemberName(String username);


    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

}
