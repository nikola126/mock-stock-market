package com.stock.backend.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.stock.backend.config.ApiConfiguration;
import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.StockDTO;
import com.stock.backend.models.Stock;
import com.stock.backend.repositories.StockRepository;
import com.stock.backend.services.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
    @InjectMocks
    StockService stockService;
    @Mock
    StockRepository stockRepository;
    @Mock
    ApiController apiController;

    ApiConfiguration apiConfiguration;
    QuoteRequestDTO quoteRequestDTO;
    StockDTO stockDTO;

    @Captor
    ArgumentCaptor<QuoteRequestDTO> quoteRequestDTOArgumentCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        apiConfiguration = new ApiConfiguration();
        stockService = new StockService(stockRepository, apiController);

        quoteRequestDTO = new QuoteRequestDTO();
        stockDTO = new StockDTO();
    }

    @Test
    void returnListOfAllStocks() {
        Mockito.when(stockRepository.findAll()).thenReturn(new ArrayList<>());
        List<Stock> stockList = stockService.getAllStocks();

        verify(stockRepository).findAll();
    }

    @Test
    void triggerAnApiCallWhenRequestingStock() {
        Mockito.when(apiController.getQuote(any())).thenReturn(new QuoteDTO());
        stockService.getStock(quoteRequestDTO);

        verify(apiController).getQuote(quoteRequestDTO);
    }

    @Test
    void attachDefaultApiTokenWhenTokenIsMissing() {
        Mockito.when(apiController.getQuote(quoteRequestDTOArgumentCaptor.capture())).thenReturn(new QuoteDTO());
        stockService.getStock(quoteRequestDTO);

        assertEquals(apiConfiguration.getDefaultToken(), quoteRequestDTOArgumentCaptor.getValue().getToken());
    }

    @Test
    void updatePriceInRepositoryWhenRequestingStock() {
        Mockito.when(apiController.getQuote(any())).thenReturn(new QuoteDTO());
        Mockito.when(stockRepository.getBySymbol(any())).thenReturn(Optional.of(new Stock()));
        stockService.getStock(quoteRequestDTO);

        verify(stockRepository).save(any());
    }

    @Test
    void saveNewStockInRepository() {
        Mockito.when(apiController.getQuote(any())).thenReturn(new QuoteDTO());
        Mockito.when(stockRepository.getBySymbol(any())).thenReturn(Optional.empty());
        stockService.saveOrUpdateStock(stockDTO);

        verify(stockRepository).save(any());
    }

    @Test
    void updatePriceInStockRepositoryWhenUpdatingStock() {
        Mockito.when(stockRepository.getBySymbol(any())).thenReturn(Optional.of(new Stock()));
        stockService.saveOrUpdateStock(stockDTO);

        verify(stockRepository).save(any());
    }

}
