package com.stock.backend.services;

import java.sql.Date;
import java.util.List;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.enums.Actions;
import com.stock.backend.models.Transaction;
import com.stock.backend.repositories.TransactionRepository;
import com.stock.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionDTO> getAllForUser(UserDTO userDTO) {
        List<Transaction> transactions = transactionRepository.getAllByUserId(userDTO.getId());

        return transactions.stream().map(Transaction::mapToDTO).toList();
    }

    public List<TransactionDTO> addTransaction(TransactionDTO transactionDTO) {
        Transaction newTransaction = new Transaction();
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setId(transactionDTO.getUserId());

        newTransaction.setUser(userRepository.getById(transactionDTO.getUserId()));
        newTransaction.setAction(Actions.valueOf(transactionDTO.getAction()));
        newTransaction.setShares(transactionDTO.getShares());
        newTransaction.setValue(transactionDTO.getValue());
        newTransaction.setDate(new Date(System.currentTimeMillis()));
        transactionRepository.save(newTransaction);

        return getAllForUser(newUserDTO);
    }
}
