package com.stock.backend.controllers;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.TransactionSummaryDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import com.stock.backend.exceptions.InsufficientAssetsException;
import com.stock.backend.exceptions.InsufficientFundsException;
import com.stock.backend.exceptions.InvalidActionException;
import com.stock.backend.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping(path = "/get")
    public Page<TransactionDTO> getAllForUser(@RequestBody TransactionSummaryDTO transactionSummaryDTO,
                                              @PageableDefault(size = 20, sort = {
                                                  "date"}, direction = Sort.Direction.DESC)
                                                  Pageable pageable
    ) {
        return transactionService.getAllForUser(transactionSummaryDTO, pageable);
    }

    @PostMapping(path = "/get/unpaged")
    public Page<TransactionDTO> getAllForUserUnpaged(@RequestBody TransactionSummaryDTO transactionSummaryDTO) {
        return transactionService.getAllForUser(transactionSummaryDTO, Pageable.unpaged());
    }

    @PostMapping(path = "/add")
    public void addTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            transactionService.addTransaction(transactionDTO);
        } catch (InsufficientFundsException | InsufficientAssetsException | InvalidActionException | ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
