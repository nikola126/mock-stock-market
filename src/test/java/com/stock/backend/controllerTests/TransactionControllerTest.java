package com.stock.backend.controllerTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import com.stock.backend.controllers.TransactionController;
import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import com.stock.backend.exceptions.InsufficientAssetsException;
import com.stock.backend.exceptions.InsufficientFundsException;
import com.stock.backend.exceptions.InvalidActionException;
import com.stock.backend.services.TransactionService;
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
public class TransactionControllerTest {
    @InjectMocks
    TransactionController transactionController;
    @Mock
    TransactionService transactionService;

    UserDTO userDTO;
    TransactionDTO transactionDTO;

    @Captor
    ArgumentCaptor<UserDTO> userDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<TransactionDTO> transactionDTOArgumentCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        transactionDTO = new TransactionDTO();
    }

    @Test
    void getAllTransactions() {
        Mockito.when(transactionService.getAllForUser(userDTOArgumentCaptor.capture())).thenReturn(new ArrayList<>());

        transactionController.getAllForUser(userDTO);
        verify(transactionService).getAllForUser(userDTO);
    }

    @Test
    void addTransaction()
        throws InvalidActionException, InsufficientFundsException, InsufficientAssetsException, ApiException {
        Mockito.when(transactionService.addTransaction(transactionDTOArgumentCaptor.capture()))
            .thenReturn(new ArrayList<>());

        transactionController.addTransaction(transactionDTO);
        verify(transactionService).addTransaction(transactionDTO);
    }

    @Test
    void throwExceptionOnTransactionWithInvalidData()
        throws InvalidActionException, InsufficientFundsException, InsufficientAssetsException, ApiException {
        Mockito.when(transactionService.addTransaction(transactionDTOArgumentCaptor.capture()))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        assertThrows(ResponseStatusException.class, () -> {
            transactionController.addTransaction(transactionDTO);
        });
    }
}
