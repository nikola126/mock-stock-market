package com.stock.backend.controllerTests;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import com.stock.backend.controllers.AssetController;
import com.stock.backend.dtos.AssetDTO;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.UserDTO;
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

@ExtendWith(MockitoExtension.class)
public class AssetControllerTest {
    @InjectMocks
    AssetController assetController;
    @Mock
    AssetService assetService;

    UserDTO userDTO;

    @Captor
    ArgumentCaptor<UserDTO> userDTOArgumentCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
    }

    @Test
    void getAssets() {
        Mockito.when(assetService.getAllForUser(userDTOArgumentCaptor.capture())).thenReturn(new ArrayList<>());
        assetController.getAllForUser(userDTO);

        verify(assetService).getAllForUser(userDTO);
    }
}
