package com.stock.backend.controllers;

import java.util.List;

import com.stock.backend.config.ApiConfiguration;
import com.stock.backend.dtos.HistoryDTO;
import com.stock.backend.dtos.HistoryRequestDTO;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.StockDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import com.stock.backend.models.Stock;
import com.stock.backend.services.StockService;
import com.stock.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
public class StockController {
    private final ApiConfiguration apiConfiguration = new ApiConfiguration();
    @Autowired
    private ApiController apiController;
    @Autowired
    private StockService stockService;
    @Autowired
    private UserService userService;

    @PostMapping(path = "/get")
    public QuoteDTO getStock(@RequestBody QuoteRequestDTO quoteRequestDTO) {
        return stockService.getStock(quoteRequestDTO);
    }

    @PostMapping(path = "/history")
    public HistoryDTO getHistory(@RequestBody HistoryRequestDTO historyRequestDTO) {
        HistoryDTO historicalDataDTO = new HistoryDTO();

        if (historyRequestDTO.getUserId() != null) {
            String userToken = userService.getById(historyRequestDTO.getUserId()).getApiToken();
            if (userToken != null) {
                historyRequestDTO.setApiToken(userService.getById(historyRequestDTO.getUserId()).getApiToken());
            } else {
                historyRequestDTO.setApiToken(apiConfiguration.getDefaultToken());
            }
        } else {
            historyRequestDTO.setApiToken(apiConfiguration.getDefaultToken());
        }

        try {
            historicalDataDTO = apiController.apiHistory(historyRequestDTO);
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return historicalDataDTO;
    }

    @PostMapping(path = "/save")
    public StockDTO saveStock(@RequestBody StockDTO stockDTO) {
        return stockService.saveOrUpdateStock(stockDTO, false).mapToDTO();
    }

    @PostMapping(path = "/update")
    public StockDTO updateStock(@RequestBody StockDTO stockDTO) {
        return stockService.saveOrUpdateStock(stockDTO, false).mapToDTO();
    }

    @GetMapping(path = "/all")
    public List<StockDTO> getAllStocks() {
        return stockService.getAllStocks().stream().map(Stock::mapToDTO).toList();
    }
}
