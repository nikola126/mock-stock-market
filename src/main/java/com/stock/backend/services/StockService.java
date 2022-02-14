package com.stock.backend.services;

import java.sql.Date;
import java.util.Optional;

import com.stock.backend.dtos.StockDTO;
import com.stock.backend.models.Stock;
import com.stock.backend.repositories.StockRepository;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public StockDTO saveOrUpdateStock(StockDTO stockDTO) {
        Optional<Stock> previouslySaved = stockRepository.findBySymbol(stockDTO.getSymbol());

        if (previouslySaved.isEmpty()) {
            Stock newStock = new Stock();
            newStock.setName(stockDTO.getName());
            newStock.setSymbol(stockDTO.getSymbol());
            newStock.setLastUpdate(new Date(System.currentTimeMillis()));
            newStock.setPrice(stockDTO.getPrice());

            stockRepository.save(newStock);
            return newStock.mapToDTO();
        } else {
            previouslySaved.get().setLastUpdate(new Date(System.currentTimeMillis()));
            return previouslySaved.get().mapToDTO();
        }
    }
}
