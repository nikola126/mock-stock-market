package com.stock.backend.repositories;

import java.util.List;
import java.util.Optional;

import com.stock.backend.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> getBySymbol(String symbol);

    Optional<Stock> getByName(String name);

    @Query(nativeQuery = true,
        value = "SELECT * FROM stocks WHERE "
            + "(:currentTime - last_update) > :timeIntervalMs "
            + "LIMIT :limit")
    List<Stock> getStaleStocks(Long currentTime, Integer timeIntervalMs, Integer limit);
}
