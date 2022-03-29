package com.stock.backend.repositories;

import java.util.List;
import java.util.Optional;

import com.stock.backend.models.Asset;
import com.stock.backend.models.HotListEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> getByUserId(Long userId);

    Optional<Asset> getByUserIdAndStockId(Long userId, Long stockId);

    @Query(nativeQuery = true,
        value = " " +
            "SELECT COUNT(a.company_id) AS usersOwning, s.name AS stockName, s.symbol AS stockSymbol " +
            "FROM assets AS a " +
            "RIGHT JOIN stocks AS s ON s.id = a.company_id " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(a.company_id) DESC ")
    Page<HotListEntry> getHotlist(Pageable pageable);
}
