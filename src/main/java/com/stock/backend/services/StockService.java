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
import com.stock.backend.models.User;
import com.stock.backend.repositories.StockRepository;
import com.stock.backend.repositories.UserRepository;
import io.micrometer.core.annotation.Counted;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final ApiController apiController;

    private final ApiConfiguration apiConfiguration = new ApiConfiguration();

    private final Integer updateTimeIntervalMs = 1000 * 60 * 15;

    public StockService(UserRepository userRepository, StockRepository stockRepository, ApiController apiController) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.apiController = apiController;
    }

    @Counted(value = "StockGet.Count", description = "Number of Stock Get requests")
    public QuoteDTO getStock(QuoteRequestDTO quoteRequestDTO) {
        // attach API token if userId is provided
        if (quoteRequestDTO.getUserId() != null) {
            User knownUser = userRepository.getById(quoteRequestDTO.getUserId());

            if (knownUser != null && knownUser.getApiToken() != null) {
                quoteRequestDTO.setToken(knownUser.getApiToken());
            } else {
                quoteRequestDTO.setToken(apiConfiguration.getDefaultToken());
            }

        } else {
            quoteRequestDTO.setToken(apiConfiguration.getDefaultToken());
        }

        // trigger an API call
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

    @Counted(value = "StockSaveUpdate.Count", description = "Number of Stock Save/Update requests")
    public Stock saveOrUpdateStock(StockDTO stockDTO, Boolean afterTransaction) {
        Optional<Stock> previouslySaved = stockRepository.getBySymbol(stockDTO.getSymbol());

        if (previouslySaved.isEmpty() && afterTransaction) {
            // following a transaction, the stockDTO contains the latest data
            // just save in repository
            Stock newStock = new Stock();
            newStock.setName(stockDTO.getName());
            newStock.setSymbol(stockDTO.getSymbol());
            newStock.setLastUpdate(System.currentTimeMillis());
            newStock.setPrice(stockDTO.getPrice());

            stockRepository.save(newStock);
            return newStock;
        } else if (previouslySaved.isEmpty()) {
            // set with the latest data and save in repository
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
        } else if (!afterTransaction) {
            // user has triggered a manual API update
            // set with the latest data and save in repository
            // TODO Attach personal API token
            QuoteRequestDTO quoteRequestDTO = new QuoteRequestDTO();
            quoteRequestDTO.setSymbol(stockDTO.getSymbol());
            quoteRequestDTO.setToken(apiConfiguration.getDefaultToken());

            QuoteDTO quoteDTO = apiController.getQuote(quoteRequestDTO);

            previouslySaved.get().setPrice(quoteDTO.getLatestPrice());
            previouslySaved.get().setLastUpdate(System.currentTimeMillis());

            stockRepository.save(previouslySaved.get());
            return previouslySaved.get();
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
