package com.stock.backend.controllerTests;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import com.stock.backend.controllers.AssetController;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AssetControllerTest {
    @InjectMocks
    AssetController assetController;
    @Mock
    AssetService assetService;

    UserDTO userDTO;

    @Captor
    ArgumentCaptor<UserDTO> userDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;

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

    @Test
    void getHotlist() {
        Page hotlistPage = Mockito.mock(Page.class);
        Pageable pageable = Mockito.mock(Pageable.class);

        Mockito.when(assetService.getHotlist(pageableArgumentCaptor.capture())).thenReturn(hotlistPage);
        assetController.getHotlist(pageable);

        verify(assetService).getHotlist(pageable);
    }
}
