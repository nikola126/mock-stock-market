package com.stock.backend.services;

import java.util.List;
import java.util.Optional;

import com.stock.backend.config.ApiConfiguration;
import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.StockDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import com.stock.backend.models.Stock;
import com.stock.backend.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StockService {
    private final StockRepository stockRepository;
    private final ApiController apiController;

    private final ApiConfiguration apiConfiguration = new ApiConfiguration();

    private final Integer updateTimeIntervalMs = 1000 * 60 * 15;

    public StockService(StockRepository stockRepository, ApiController apiController) {
        this.stockRepository = stockRepository;
        this.apiController = apiController;
    }

    public QuoteDTO getStock(QuoteRequestDTO quoteRequestDTO) {
        // trigger an API call
        if (quoteRequestDTO.getToken() == null) {
            quoteRequestDTO.setToken(apiConfiguration.getDefaultToken());
        }
        QuoteDTO quoteDTO = new QuoteDTO();
        quoteDTO = apiController.getQuote(quoteRequestDTO);

        Optional<Stock> previouslySaved = stockRepository.getBySymbol(quoteRequestDTO.getSymbol());
        if (previouslySaved.isPresent()) {
            // update data in repository
            previouslySaved.get().setLastUpdate(System.currentTimeMillis());
            previouslySaved.get().setPrice(quoteDTO.getLatestPrice());
            stockRepository.save(previouslySaved.get());
        }

        // return QuoteDTO
        return quoteDTO;
    }

    public Stock saveOrUpdateStock(StockDTO stockDTO) {
        Optional<Stock> previouslySaved = stockRepository.getBySymbol(stockDTO.getSymbol());

        if (previouslySaved.isEmpty()) {
            Stock newStock = new Stock();
            QuoteRequestDTO quoteRequestDTO = new QuoteRequestDTO();
            quoteRequestDTO.setSymbol(stockDTO.getSymbol());
            quoteRequestDTO.setToken(apiConfiguration.getDefaultToken());

            QuoteDTO quoteDTO = apiController.getQuote(quoteRequestDTO);

            newStock.setName(quoteDTO.getCompanyName());
            newStock.setSymbol(quoteDTO.getSymbol());
            newStock.setLastUpdate(System.currentTimeMillis());
            newStock.setPrice(quoteDTO.getLatestPrice());

            stockRepository.save(newStock);
            return newStock;
        } else {
            previouslySaved.get().setPrice(stockDTO.getPrice());
            previouslySaved.get().setLastUpdate(System.currentTimeMillis());

            stockRepository.save(previouslySaved.get());
            return previouslySaved.get();
        }
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    // trigger API call periodically, updating stocks older than updateTimeIntervalMs
    @Scheduled(fixedDelay = 1000 * 60, initialDelay = 1000 * 15)
    public void scheduledStockUpdate() {
        QuoteRequestDTO quoteRequestDTO = new QuoteRequestDTO();
        List<Stock> staleStocks = stockRepository.getStaleStocks(System.currentTimeMillis(), updateTimeIntervalMs, 1);

        for (Stock stock : staleStocks) {
            quoteRequestDTO.setSymbol(stock.getSymbol());

            try {
                QuoteDTO quoteDTO = apiController.apiQuote(quoteRequestDTO);
                stock.setPrice(quoteDTO.getLatestPrice());
                stock.setLastUpdate(System.currentTimeMillis());
                stockRepository.save(stock);
            } catch (ApiException ignored) {
            }
        }
    }

}
