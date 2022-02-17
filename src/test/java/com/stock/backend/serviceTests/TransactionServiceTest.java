package com.stock.backend.serviceTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import com.stock.backend.exceptions.InsufficientAssetsException;
import com.stock.backend.exceptions.InsufficientFundsException;
import com.stock.backend.exceptions.InvalidActionException;
import com.stock.backend.models.Asset;
import com.stock.backend.models.Stock;
import com.stock.backend.models.Transaction;
import com.stock.backend.models.User;
import com.stock.backend.repositories.AssetRepository;
import com.stock.backend.repositories.StockRepository;
import com.stock.backend.repositories.TransactionRepository;
import com.stock.backend.repositories.UserRepository;
import com.stock.backend.services.AssetService;
import com.stock.backend.services.StockService;
import com.stock.backend.services.TransactionService;
import com.stock.backend.services.UserService;
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
public class TransactionServiceTest {
    @InjectMocks
    TransactionService transactionService;
    @Mock
    ApiController apiController;
    @Mock
    StockService stockService;
    @Mock
    UserService userService;
    @Mock
    AssetService assetService;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    StockRepository stockRepository;
    @Mock
    AssetRepository assetRepository;
    @Mock
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<Long> longArgumentCaptor;

    UserDTO userDTO;
    TransactionDTO transactionDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        transactionService =
            new TransactionService(transactionRepository, userService, stockService, stockRepository, assetService,
                assetRepository, apiController);

        userDTO = new UserDTO();
        transactionDTO = new TransactionDTO();

    }

    @Test
    void returnListOfAllTransactions() {
        Mockito.when(transactionRepository.getAllByUserId(longArgumentCaptor.capture())).thenReturn(new ArrayList<>());

        transactionService.getAllForUser(userDTO);
        verify(transactionRepository).getAllByUserId(userDTO.getId());
    }

    @Test
    void throwExceptionOnInvalidAction() {
        transactionDTO.setAction("");

        assertThrows(InvalidActionException.class, () -> {
            transactionService.addTransaction(transactionDTO);
        });
    }

    @Test
    void throwExceptionOnNegativeShares() {
        transactionDTO.setAction("BUY");
        transactionDTO.setShares(-1);

        assertThrows(InvalidActionException.class, () -> {
            transactionService.addTransaction(transactionDTO);
        });
    }

    @Test
    void updateShareValueFromApiWhenNoDataInRepository()
        throws ApiException, InvalidActionException, InsufficientFundsException, InsufficientAssetsException {
        TransactionDTO mockedTransactionDTO = new TransactionDTO();
        mockedTransactionDTO.setAction("BUY");
        mockedTransactionDTO.setShares(1);
        mockedTransactionDTO.setPrice(1.0);
        mockedTransactionDTO.setSymbol(" ");
        QuoteDTO mockedQuoteDTO = new QuoteDTO();
        mockedQuoteDTO.setLatestPrice(10.0);
        User mockedUser = new User();
        mockedUser.setCapital(10.0);

        Mockito.when(userService.getById(any())).thenReturn(mockedUser);
        Mockito.when(stockRepository.getBySymbol(anyString())).thenReturn(Optional.empty(), Optional.of(new Stock()));
        Mockito.when(apiController.apiQuote(any())).thenReturn(mockedQuoteDTO);
        Mockito.when(stockService.saveOrUpdateStock(any())).thenReturn(new Stock());

        transactionService.addTransaction(mockedTransactionDTO);

        verify(apiController).apiQuote(any());
    }

    @Test
    void throwExceptionWhenSellingWithNoShares()
        throws ApiException {
        TransactionDTO mockedTransactionDTO = new TransactionDTO();
        mockedTransactionDTO.setAction("SELL");
        mockedTransactionDTO.setShares(1);
        mockedTransactionDTO.setPrice(1.0);
        mockedTransactionDTO.setSymbol(" ");

        QuoteDTO mockedQuoteDTO = new QuoteDTO();
        mockedQuoteDTO.setLatestPrice(10.0);

        User mockedUser = new User();
        mockedUser.setCapital(10.0);

        Mockito.when(userService.getById(any())).thenReturn(mockedUser);
        Mockito.when(stockRepository.getBySymbol(anyString())).thenReturn(Optional.empty(), Optional.of(new Stock()));
        Mockito.when(apiController.apiQuote(any())).thenReturn(mockedQuoteDTO);
        assertThrows(InsufficientAssetsException.class, () -> {
            transactionService.addTransaction(mockedTransactionDTO);
        });
    }

    @Test
    void throwExceptionWhenSellingWithNotEnoughShares() {
        TransactionDTO mockedTransactionDTO = new TransactionDTO();
        mockedTransactionDTO.setAction("SELL");
        mockedTransactionDTO.setShares(5);
        mockedTransactionDTO.setPrice(1.0);
        mockedTransactionDTO.setSymbol(" ");

        QuoteDTO mockedQuoteDTO = new QuoteDTO();
        mockedQuoteDTO.setLatestPrice(10.0);

        User mockedUser = new User();
        mockedUser.setCapital(10.0);

        Asset asset = new Asset();
        asset.setShares(1);

        Stock mockedStock = new Stock();
        mockedStock.setSymbol(" ");
        mockedStock.setId(1L);

        Mockito.when(userService.getById(any())).thenReturn(mockedUser);
        Mockito.when(stockRepository.getBySymbol(anyString()))
            .thenReturn(Optional.of(mockedStock), Optional.of(mockedStock), Optional.of(mockedStock));
        Mockito.when(assetRepository.getByUserIdAndStockId(any(), any())).thenReturn(Optional.of(asset));

        assertThrows(InsufficientAssetsException.class, () -> {
            transactionService.addTransaction(mockedTransactionDTO);
        });
    }

    @Test
    void throwExceptionWhenBuyingWithNotEnoughCapital() throws ApiException {
        TransactionDTO mockedTransactionDTO = new TransactionDTO();
        mockedTransactionDTO.setAction("BUY");
        mockedTransactionDTO.setShares(1);
        mockedTransactionDTO.setPrice(10.0);
        mockedTransactionDTO.setSymbol(" ");

        QuoteDTO mockedQuoteDTO = new QuoteDTO();
        mockedQuoteDTO.setLatestPrice(10.0);

        User mockedUser = new User();
        mockedUser.setCapital(5.0);

        Asset asset = new Asset();
        asset.setShares(1);

        Stock mockedStock = new Stock();
        mockedStock.setSymbol("test");
        mockedStock.setId(1L);

        Mockito.when(userService.getById(any())).thenReturn(mockedUser);
        Mockito.when(stockRepository.getBySymbol(anyString()))
            .thenReturn(Optional.empty(), Optional.of(mockedStock), Optional.of(mockedStock));
        Mockito.when(apiController.apiQuote(any())).thenReturn(mockedQuoteDTO);

        assertThrows(InsufficientFundsException.class, () -> {
            transactionService.addTransaction(mockedTransactionDTO);
        });
    }
}
