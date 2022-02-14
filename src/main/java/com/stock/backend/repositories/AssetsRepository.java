package com.stock.backend.repositories;

import java.util.List;

import com.stock.backend.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetsRepository extends JpaRepository<Asset, Long> {

    List<Asset> getByUserId(Long userId);
}
