package com.stock.backend.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.enums.Actions;
import com.stock.backend.models.Asset;
import com.stock.backend.models.Stock;
import com.stock.backend.models.User;
import com.stock.backend.repositories.AssetRepository;
import com.stock.backend.repositories.StockRepository;
import com.stock.backend.repositories.TransactionRepository;
import com.stock.backend.repositories.UserRepository;
import com.stock.backend.services.AssetService;
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

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {
    @InjectMocks
    AssetService assetService;
    @Mock
    AssetRepository assetRepository;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    StockRepository stockRepository;
    @Mock
    UserRepository userRepository;

    UserDTO userDTO;
    TransactionDTO transactionDTO;
    Asset asset;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    ArgumentCaptor<Long> longArgumentCaptor;
    @Captor
    ArgumentCaptor<Asset> assetArgumentCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        assetService = new AssetService(assetRepository, transactionRepository, stockRepository, userRepository);

        asset = new Asset();
        asset.setUser(new User());
        asset.setStock(new Stock());
        userDTO = new UserDTO();
        transactionDTO = new TransactionDTO();
    }

    @Test
    void returnListOfAssets() {
        ArrayList<Asset> nonEmptyListOfAssets = new ArrayList<>();
        Stock stock = new Stock();
        stock.setPrice(0.0);
        Asset asset = new Asset();
        asset.setStock(stock);
        asset.setShares(0);
        nonEmptyListOfAssets.add(asset);

        Mockito.when(assetRepository.getByUserId(longArgumentCaptor.capture())).thenReturn(nonEmptyListOfAssets);

        List<Asset> assets = assetService.getAllForUser(userDTO);

        assertEquals(nonEmptyListOfAssets.size(), assets.size());
    }

    @Test
    void returnEmptyListWhenNoAssets() {
        ArrayList<Asset> nonEmptyListOfAssets = new ArrayList<>();
        Mockito.when(assetRepository.getByUserId(longArgumentCaptor.capture())).thenReturn(nonEmptyListOfAssets);

        List<Asset> assets = assetService.getAllForUser(userDTO);

        assertEquals(0, assets.size());
    }

    @Test
    void saveAnAsset() {
        Mockito.when(userRepository.getById(longArgumentCaptor.capture())).thenReturn(new User());
        Mockito.when(stockRepository.getBySymbol(stringArgumentCaptor.capture())).thenReturn(Optional.of(new Stock()));
        assetService.saveAsset(userDTO, transactionDTO);

        verify(assetRepository).save(asset);
    }

    @Test
    void saveAnAssetAfterTransaction() {
        User mockedUser = new User();
        asset.setUser(mockedUser);
        asset.setShares(5);
        asset.setStock(new Stock());
        transactionDTO.setShares(5);
        transactionDTO.setAction(Actions.SELL.toString());

        Mockito.when(userRepository.getById(longArgumentCaptor.capture())).thenReturn(mockedUser);
        Mockito.when(stockRepository.getBySymbol(stringArgumentCaptor.capture())).thenReturn(Optional.of(new Stock()));
        Mockito.when(assetRepository.getByUserIdAndStockId(longArgumentCaptor.capture(), longArgumentCaptor.capture()))
            .thenReturn(Optional.empty());
        assetService.saveOrUpdateAsset(userDTO, transactionDTO);

        verify(assetRepository).save(asset);
    }

    @Test
    void editAnAssetAfterTransaction() {
        asset.setShares(5);
        transactionDTO.setShares(5);
        transactionDTO.setAction(Actions.BUY.toString());

        Mockito.when(stockRepository.getBySymbol(stringArgumentCaptor.capture())).thenReturn(Optional.of(new Stock()));
        Mockito.when(assetRepository.getByUserIdAndStockId(longArgumentCaptor.capture(), longArgumentCaptor.capture()))
            .thenReturn(Optional.of(asset));
        assetService.saveOrUpdateAsset(userDTO, transactionDTO);

        verify(assetRepository).save(asset);
    }

    @Test
    void deleteAnAssetAfterTransaction() {
        asset.setShares(5);
        transactionDTO.setShares(5);
        transactionDTO.setAction(Actions.SELL.toString());

        Mockito.when(stockRepository.getBySymbol(stringArgumentCaptor.capture())).thenReturn(Optional.of(new Stock()));
        Mockito.when(assetRepository.getByUserIdAndStockId(longArgumentCaptor.capture(), longArgumentCaptor.capture()))
            .thenReturn(Optional.of(asset));
        assetService.saveOrUpdateAsset(userDTO, transactionDTO);

        verify(assetRepository).delete(asset);
    }

    @Test
    void getHotList() {
        Page page = Mockito.mock(Page.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        Mockito.when(assetRepository.getHotlist(pageable)).thenReturn(page);
        assetService.getHotlist(pageable);

        verify(assetRepository).getHotlist(pageable);
    }


}
