package com.stock.backend.repositories;

import java.util.List;

import com.stock.backend.models.NetWorth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NetWorthRepository extends JpaRepository<NetWorth, Long> {

    List<NetWorth> findByUserIdOrderByDateAsc(Long userId);

    @Query(nativeQuery = true,
    value = "SELECT COALESCE(SUM(shares * price), 0) " +
        "FROM assets as a JOIN stocks as s " +
        "ON s.id = a.company_id " +
        "WHERE user_id = :userId")
    Double getNetworth(Long userId);
}
