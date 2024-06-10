package com.project.watermelon.repository;

import com.project.watermelon.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    List<Concert> findAll();
}
