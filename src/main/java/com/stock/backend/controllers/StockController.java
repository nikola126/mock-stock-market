package com.stock.backend.controllers;

import java.util.List;

import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.StockDTO;
import com.stock.backend.models.Stock;
import com.stock.backend.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
public class StockController {
    @Autowired
    private StockService stockService;

    @PostMapping(path = "/get")
    public QuoteDTO getStock(@RequestBody QuoteRequestDTO quoteRequestDTO) {
        return stockService.getStock(quoteRequestDTO);
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
