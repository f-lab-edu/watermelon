package com.project.watermelon.repository;

import com.project.watermelon.model.Purchase;
import com.project.watermelon.enumeration.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p " +
            "JOIN p.ticket t " +
            "WHERE t.concertMapping.concertMappingId = :concertMappingId " +
            "AND p.purchaseStatus IN :statuses")
    List<Purchase> findByConcertMappingIdAndStatuses(@Param("concertMappingId") Long concertMappingId,
                                                     @Param("statuses") List<PurchaseStatus> statuses);
}
