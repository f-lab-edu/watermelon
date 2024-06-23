package com.project.watermelon.repository;

import com.project.watermelon.model.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    Page<Concert> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
