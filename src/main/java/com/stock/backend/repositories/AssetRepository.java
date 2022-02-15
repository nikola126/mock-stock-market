package com.stock.backend.repositories;

import java.util.List;
import java.util.Optional;

import com.stock.backend.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> getByUserId(Long userId);

    Optional<Asset> getByUserIdAndStockId(Long userId, Long stockId);
}
