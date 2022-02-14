package com.stock.backend.repositories;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.stock.backend.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);

    Optional<Stock> findByName(String name);

    @Query(nativeQuery = true, value = "SELECT * FROM stock WHERE ABS(DATEDIFF(hour, :currentDate, last_update)) < :hourInterval")
    List<Stock> getStaleStocks(Date currentDate, Integer hourInterval);
}
