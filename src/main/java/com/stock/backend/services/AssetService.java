package com.stock.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.enums.Actions;
import com.stock.backend.models.Asset;
import com.stock.backend.models.Stock;
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
        Optional<Stock> stockOptional = stockRepository.getByName(transactionDTO.getCompanyName());
        if (stockOptional.isEmpty()) {
            return;
        }

        Stock stock = stockOptional.get();

        Optional<Asset> assetOptional = assetsRepository.getByUserIdAndStockId(userDTO.getId(), stock.getId());
        if (assetOptional.isEmpty()) {
            saveAsset(userDTO, transactionDTO);
            return;
        }
        Asset assetToEdit = assetOptional.get();

        // delete asset if all shares are sold
        if (Objects.equals(transactionDTO.getShares(), assetToEdit.getShares())
            && Objects.equals(transactionDTO.getAction(), Actions.SELL.name())) {
            assetsRepository.delete(assetToEdit);
        } else {
            if (Objects.equals(transactionDTO.getAction(), Actions.SELL.name())) {
                assetToEdit.setShares(assetToEdit.getShares() - transactionDTO.getShares());
            } else {
                assetToEdit.setShares(assetToEdit.getShares() + transactionDTO.getShares());
            }
            assetsRepository.save(assetToEdit);
        }
    }

}
