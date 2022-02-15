package com.stock.backend.controllers;

import java.util.List;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.InsufficientAssetsException;
import com.stock.backend.exceptions.InsufficientFundsException;
import com.stock.backend.exceptions.InvalidActionException;
import com.stock.backend.models.Transaction;
import com.stock.backend.services.TransactionService;
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
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping(path = "/get")
    public List<TransactionDTO> getAllForUser(@RequestBody UserDTO userDTO) {
        return transactionService.getAllForUser(userDTO).stream().map(Transaction::mapToDTO).toList();
    }

    @PostMapping(path = "/add")
    public List<TransactionDTO> addTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            return transactionService.addTransaction(transactionDTO).stream().map(Transaction::mapToDTO).toList();
        } catch (InsufficientFundsException | InsufficientAssetsException | InvalidActionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
