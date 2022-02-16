package com.stock.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.enums.Actions;
import com.stock.backend.models.Asset;
import com.stock.backend.models.Stock;
import com.stock.backend.models.User;
import com.stock.backend.repositories.AssetRepository;
import com.stock.backend.repositories.StockRepository;
import com.stock.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AssetService {
    private final AssetRepository assetsRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public AssetService(AssetRepository assetsRepository, StockRepository stockRepository,
                        UserRepository userRepository) {
        this.assetsRepository = assetsRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
    }

    public List<Asset> getAllForUser(UserDTO userDTO) {
        List<Asset> assetList = assetsRepository.getByUserId(userDTO.getId());
        return assetList;
    }

    public void saveAsset(UserDTO userDTO, TransactionDTO transactionDTO) {
        Asset newAsset = new Asset();

        newAsset.setUser(userRepository.getById(userDTO.getId()));
        newAsset.setStock(stockRepository.getBySymbol(transactionDTO.getSymbol()).get());
        newAsset.setShares(transactionDTO.getShares());

        assetsRepository.save(newAsset);
    }

    public void saveOrUpdateAsset(UserDTO userDTO, TransactionDTO transactionDTO) {

        Optional<Stock> stockOptional = stockRepository.getBySymbol(transactionDTO.getSymbol());
        if (stockOptional.isEmpty()) {
            return;
        }

        Stock stock = stockOptional.get();

        Optional<Asset> assetOptional = assetsRepository.getByUserIdAndStockId(userDTO.getId(), stock.getId());
        if (assetOptional.isEmpty()) {
            // Save the new asset
            Asset newAsset = new Asset();
            User userRef = userRepository.getById(userDTO.getId());
            Stock stockRef = stockRepository.getBySymbol(transactionDTO.getSymbol()).get();

            newAsset.setUser(userRef);
            newAsset.setStock(stockRef);
            newAsset.setShares(transactionDTO.getShares());
            assetsRepository.save(newAsset);
        } else {
            // Edit asset
            Asset assetToEdit = assetOptional.get();

            // delete asset if all shares are sold
            if (Objects.equals(transactionDTO.getShares(), assetToEdit.getShares())
                && Objects.equals(transactionDTO.getAction(), Actions.SELL.toString())) {
                assetsRepository.delete(assetToEdit);
            } else {
                if (Objects.equals(transactionDTO.getAction(), Actions.SELL.toString())) {
                    assetToEdit.setShares(assetToEdit.getShares() - transactionDTO.getShares());
                } else {
                    assetToEdit.setShares(assetToEdit.getShares() + transactionDTO.getShares());
                }

                assetsRepository.save(assetToEdit);
            }
        }

    }

}
