package com.stock.backend.services;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.stock.backend.dtos.StockDTO;
import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.enums.Actions;
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

    public TransactionService(TransactionRepository transactionRepository, UserService userService,
                              StockService stockService, StockRepository stockRepository, AssetService assetService,
                              AssetRepository assetRepository) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.stockService = stockService;
        this.stockRepository = stockRepository;
        this.assetService = assetService;
        this.assetRepository = assetRepository;
    }

    public List<Transaction> getAllForUser(UserDTO userDTO) {
        List<Transaction> transactions = transactionRepository.getAllByUserId(userDTO.getId());

        return transactions;
    }

    public List<Transaction> addTransaction(TransactionDTO transactionDTO)
        throws InsufficientFundsException, InvalidActionException, InsufficientAssetsException {
        // get user
        User transactionUser = userService.getById(transactionDTO.getUserId());

        // check for valid action
        if (!Objects.equals(transactionDTO.getAction(), Actions.SELL.toString())
            && !Objects.equals(transactionDTO.getAction(), Actions.BUY.toString())) {
            throw new InvalidActionException("Invalid action provided!");
        }

        // check for sufficient capital to buy
        if (Objects.equals(transactionDTO.getAction(), Actions.BUY.toString())
            && transactionUser.getCapital() < transactionDTO.getShares() * transactionDTO.getValue()) {
            throw new InsufficientFundsException("You don't have enough funds to complete the purchase!");
        }

        // check if enough shares are owned
        Optional<Stock> optionalStock = stockRepository.getBySymbol(transactionDTO.getSymbol());

        if (optionalStock.isEmpty() && transactionDTO.getAction().equals(Actions.SELL.toString())) {
            throw new InsufficientAssetsException("You don't own any assets from " + transactionDTO.getCompanyName());
        }

        // save/update stock (transaction should always contain the latest price data)
        StockDTO newStockDTO = new StockDTO();
        newStockDTO.setName(transactionDTO.getCompanyName());
        newStockDTO.setSymbol(transactionDTO.getSymbol());
        newStockDTO.setPrice(transactionDTO.getValue());

        stockService.saveOrUpdateStock(newStockDTO);

        // check if enough assets are owned
        optionalStock = stockRepository.getBySymbol(transactionDTO.getSymbol());

        Optional<Asset> optionalAsset = assetRepository.getByUserIdAndStockId(transactionUser.getId(),
            stockRepository.getBySymbol(optionalStock.get().getSymbol()).get().getId());

        if (optionalAsset.isEmpty() && transactionDTO.getAction().equals(Actions.SELL.toString())) {
            throw new InsufficientAssetsException("You don't own any assets from " + transactionDTO.getCompanyName());
        }

//        // check for sufficient shares to sell
        if (optionalAsset.isPresent() && optionalAsset.get().getShares() < transactionDTO.getShares()) {
            throw new InsufficientAssetsException(
                "You don't own enough assets from " + transactionDTO.getCompanyName());
        }

        // save/update asset
        assetService.saveOrUpdateAsset(transactionUser.mapToDTO(), transactionDTO);

        // update capital
        Double currentCapital = transactionUser.getCapital();
        if (Objects.equals(transactionDTO.getAction(), Actions.SELL.name())) {
            transactionUser.setCapital(currentCapital + transactionDTO.getShares() * transactionDTO.getValue());
        } else if (Objects.equals(transactionDTO.getAction(), Actions.BUY.name())) {
            transactionUser.setCapital(currentCapital - transactionDTO.getShares() * transactionDTO.getValue());
        }

        // add transaction
        Transaction newTransaction = new Transaction();
        Stock newStock = stockRepository.getBySymbol(transactionDTO.getSymbol()).get();

        newTransaction.setUser(transactionUser);
        newTransaction.setStock(newStock);
        newTransaction.setAction(Actions.valueOf(transactionDTO.getAction()));
        newTransaction.setShares(transactionDTO.getShares());
        newTransaction.setValue(transactionDTO.getValue());
        newTransaction.setDate(new Date(System.currentTimeMillis()));
        transactionRepository.save(newTransaction);

        // return full transaction list
        return getAllForUser(transactionUser.mapToDTO());
    }
}
