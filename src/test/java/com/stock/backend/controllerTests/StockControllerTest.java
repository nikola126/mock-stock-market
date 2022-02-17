package com.stock.backend.controllerTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import com.stock.backend.controllers.StockController;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.StockDTO;
import com.stock.backend.models.Stock;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {
    @InjectMocks
    StockController stockController;
    @Mock
    StockService stockService;

    StockDTO stockDTO;
    QuoteRequestDTO quoteRequestDTO;

    @Captor
    ArgumentCaptor<StockDTO> stockDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<QuoteRequestDTO> quoteRequestDTOArgumentCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        stockDTO = new StockDTO();
        quoteRequestDTO = new QuoteRequestDTO();
    }

    @Test
    void getQuote() {
        Mockito.when(stockService.getStock(quoteRequestDTOArgumentCaptor.capture())).thenReturn(new QuoteDTO());
        stockController.getStock(quoteRequestDTO);

        verify(stockService).getStock(quoteRequestDTO);
    }

    @Test
    void throwExceptionWhenUnknownSymbol() {
        Mockito.when(stockService.getStock(quoteRequestDTOArgumentCaptor.capture()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        assertThrows(ResponseStatusException.class, () -> {
            stockController.getStock(quoteRequestDTO);
        });
    }

    @Test
    void getAllStocksInDB() {
        Mockito.when(stockService.getAllStocks()).thenReturn(new ArrayList<Stock>());
        stockController.getAllStocks();

        verify(stockService).getAllStocks();
    }
}
