package com.stock.backend.controllerTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.stock.backend.controllers.TransactionController;
import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.TransactionSummaryDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    TransactionSummaryDTO transactionSummaryDTO;

    @Captor
    ArgumentCaptor<UserDTO> userDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<TransactionDTO> transactionDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<TransactionSummaryDTO> transactionSummaryDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        transactionDTO = new TransactionDTO();
        transactionSummaryDTO = new TransactionSummaryDTO();
    }

    @Test
    void getAllTransactions() {
        Page transactionPage = Mockito.mock(Page.class);
        Pageable pageable = Mockito.mock(Pageable.class);
        Mockito.when(
                transactionService.getAllForUser(transactionSummaryDTOArgumentCaptor.capture(),
                    pageableArgumentCaptor.capture()))
            .thenReturn(transactionPage);

        transactionController.getAllForUser(transactionSummaryDTO, pageable);
        verify(transactionService).getAllForUser(transactionSummaryDTO, pageable);
    }

    @Test
    void addTransaction()
        throws InvalidActionException, InsufficientFundsException, InsufficientAssetsException, ApiException {

        transactionController.addTransaction(transactionDTO);
        verify(transactionService).addTransaction(transactionDTO);
    }

    @Test
    void throwExceptionOnTransactionWithInvalidData()
        throws InvalidActionException, InsufficientFundsException, InsufficientAssetsException, ApiException {

        Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
            .when(transactionService).addTransaction(transactionDTOArgumentCaptor.capture());

        assertThrows(ResponseStatusException.class, () -> {
            transactionController.addTransaction(transactionDTO);
        });
    }
}
