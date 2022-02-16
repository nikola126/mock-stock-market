package com.stock.backend.controllerTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.stock.backend.controllers.UserController;
import com.stock.backend.dtos.EditUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.exceptions.UserExceptions.UserNotFoundException;
import com.stock.backend.models.User;
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
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;

    @Captor
    ArgumentCaptor<NewUserDTO> newUserDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<LoginUserDTO> loginUserDTOArgumentCaptor;
    @Captor
    ArgumentCaptor<EditUserDTO> editUserDTOArgumentCaptor;

    NewUserDTO newUserDTO;
    LoginUserDTO loginUserDTO;
    EditUserDTO editUserDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        newUserDTO = new NewUserDTO();
        loginUserDTO = new LoginUserDTO();
        editUserDTO = new EditUserDTO();
    }

    @Test
    void registerUser() throws Exception {
        Mockito.when(userService.addNewUser(newUserDTOArgumentCaptor.capture())).thenReturn(new User());
        userController.addUser(newUserDTO);

        verify(userService).addNewUser(newUserDTO);
    }

    @Test
    void loginUser() throws Exception {
        Mockito.when(userService.login(loginUserDTOArgumentCaptor.capture())).thenReturn(new User());
        userController.login(loginUserDTO);

        verify(userService).login(loginUserDTO);
    }

    @Test
    void editUserDisplayName() throws Exception {
        Mockito.when(userService.updateDisplayName(editUserDTOArgumentCaptor.capture())).thenReturn(new User());
        editUserDTO.setNewDisplayName("newDisplayName");
        userController.edit(editUserDTO);

        verify(userService).updateDisplayName(any());
    }

    @Test
    void editUserCapital() throws Exception {
        Mockito.when(userService.updateCapital(editUserDTOArgumentCaptor.capture())).thenReturn(new User());
        editUserDTO.setCapitalChange(1000.0);
        userController.edit(editUserDTO);

        verify(userService).updateCapital(any());
    }

    @Test
    void editUserPassword() throws Exception {
        Mockito.when(userService.updatePassword(editUserDTOArgumentCaptor.capture())).thenReturn(new User());
        editUserDTO.setNewPassword("newPassword");
        userController.edit(editUserDTO);

        verify(userService).updatePassword(any());
    }

    @Test
    void loginUserWithWrongPassword() throws Exception {
        Mockito.when(userService.login(loginUserDTOArgumentCaptor.capture())).thenThrow(new UserNotFoundException(""));

        assertThrows(ResponseStatusException.class, () -> {
            userController.login(loginUserDTO);
        });
    }

    @Test
    void loginUserWithWrongUsername() throws Exception {
        Mockito.when(userService.login(loginUserDTOArgumentCaptor.capture())).thenThrow(new UserNotFoundException(""));

        assertThrows(ResponseStatusException.class, () -> {
            userController.login(loginUserDTO);
        });
    }
}
