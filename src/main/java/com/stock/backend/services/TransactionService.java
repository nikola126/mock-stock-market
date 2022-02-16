package com.stock.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.dtos.StockDTO;
import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.enums.Actions;
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
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final UserService userService;
    private final StockService stockService;
    private final StockRepository stockRepository;
    private final AssetService assetService;
    private final AssetRepository assetRepository;
    private final ApiController apiController;

    public TransactionService(TransactionRepository transactionRepository, UserService userService,
                              StockService stockService, StockRepository stockRepository, AssetService assetService,
                              AssetRepository assetRepository, ApiController apiController) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.stockService = stockService;
        this.stockRepository = stockRepository;
        this.assetService = assetService;
        this.assetRepository = assetRepository;
        this.apiController = apiController;
    }

    public List<Transaction> getAllForUser(UserDTO userDTO) {
        List<Transaction> transactions = transactionRepository.getAllByUserId(userDTO.getId());

        return transactions;
    }

    public List<Transaction> addTransaction(TransactionDTO transactionDTO)
        throws InsufficientFundsException, InvalidActionException, InsufficientAssetsException, ApiException {
        // get user
        User transactionUser = userService.getById(transactionDTO.getUserId());

        // check for valid action
        if (!Objects.equals(transactionDTO.getAction(), Actions.SELL.toString())
            && !Objects.equals(transactionDTO.getAction(), Actions.BUY.toString())) {
            throw new InvalidActionException("Invalid action provided!");
        }

        // check for valid shares
        if (transactionDTO.getShares() <= 0) {
            throw new InvalidActionException("Invalid action provided!");
        }

        // update share value from stock repository or by API call
        Optional<Stock> optionalStock = stockRepository.getBySymbol(transactionDTO.getSymbol());
        if (optionalStock.isEmpty()) {
            QuoteRequestDTO quoteRequestDTO = new QuoteRequestDTO();
            quoteRequestDTO.setSymbol(transactionDTO.getSymbol());

            try {
                QuoteDTO quoteDTO = apiController.apiQuote(quoteRequestDTO);
                transactionDTO.setPrice(quoteDTO.getLatestPrice());
            } catch (ApiException e) {
                throw new ApiException("API Exception");
            }
        } else {
            transactionDTO.setPrice(optionalStock.get().getPrice());
        }

        if (transactionDTO.getAction().equals(Actions.SELL.toString())) {
            // check if any shares are owned (should be present in the stock repository)
            if (optionalStock.isEmpty()) {
                throw new InsufficientAssetsException(
                    "You don't own any assets from " + transactionDTO.getCompanyName());
            }

            // check if enough shares are owned
            Optional<Asset> optionalAsset = assetRepository.getByUserIdAndStockId(transactionUser.getId(),
                stockRepository.getBySymbol(optionalStock.get().getSymbol()).get().getId());

            if (optionalAsset.isEmpty()) {
                throw new InsufficientAssetsException(
                    "You don't own any assets from " + transactionDTO.getCompanyName());
            }

            // check for sufficient shares to sell
            if (optionalAsset.get().getShares() < transactionDTO.getShares()) {
                throw new InsufficientAssetsException(
                    "You don't own enough assets from " + transactionDTO.getCompanyName());
            }

        } else {
            // check for sufficient capital to buy
            if (transactionUser.getCapital() < transactionDTO.getShares() * transactionDTO.getPrice()) {
                throw new InsufficientFundsException("You don't have enough capital to complete the transaction!");
            }
        }

        // save/update stock (transaction should always contain the latest price data)
        StockDTO newStockDTO = new StockDTO();

        newStockDTO.setName(transactionDTO.getCompanyName());
        newStockDTO.setSymbol(transactionDTO.getSymbol());
        newStockDTO.setPrice(transactionDTO.getPrice());
        stockService.saveOrUpdateStock(newStockDTO);

        // save/update asset
        assetService.saveOrUpdateAsset(transactionUser.mapToDTO(), transactionDTO);

        // update capital
        Double currentCapital = transactionUser.getCapital();
        if (transactionDTO.getAction().equals(Actions.SELL.toString())) {
            transactionUser.setCapital(currentCapital + transactionDTO.getShares() * transactionDTO.getPrice());
        } else {
            transactionUser.setCapital(currentCapital - transactionDTO.getShares() * transactionDTO.getPrice());
        }

        // add transaction
        Transaction newTransaction = new Transaction();
        Stock newStock = stockRepository.getBySymbol(transactionDTO.getSymbol()).get();

        newTransaction.setUser(transactionUser);
        newTransaction.setStock(newStock);
        newTransaction.setAction(Actions.valueOf(transactionDTO.getAction()));
        newTransaction.setShares(transactionDTO.getShares());
        newTransaction.setPrice(transactionDTO.getPrice());
        newTransaction.setDate(System.currentTimeMillis());
        transactionRepository.save(newTransaction);

        // return full transaction list
        return getAllForUser(transactionUser.mapToDTO());
    }
}
