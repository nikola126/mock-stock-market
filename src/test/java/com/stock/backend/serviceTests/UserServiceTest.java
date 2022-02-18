package com.stock.backend.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.EditUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.exceptions.UserExceptions.NegativeCapitalChangeException;
import com.stock.backend.exceptions.UserExceptions.SamePasswordException;
import com.stock.backend.exceptions.UserExceptions.UserNotFoundException;
import com.stock.backend.models.User;
import com.stock.backend.repositories.UserRepository;
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
public class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    ApiController apiController;
    @Mock
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<String> usernameCaptor;

    @Captor
    ArgumentCaptor<String> passwordCaptor;

    User expectedUser;

    User newUser;

    NewUserDTO newUserDTO;

    LoginUserDTO loginUserDTO;

    EditUserDTO editUserDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, apiController);

        expectedUser = new User();
        expectedUser.setUsername("testUsername");
        expectedUser.setPassword("testPassword");
        expectedUser.setDisplayName("testDisplayName");

        newUserDTO = new NewUserDTO();
        newUserDTO.setUsername("testUsername");
        newUserDTO.setPassword("testPassword");
        newUserDTO.setDisplayName("testDisplayName");

        newUser = new User();
        newUser.setUsername("testUsername");
        newUser.setPassword("testPassword");
        newUser.setDisplayName("testDisplayName");
        newUser.setCapital(1000.0);

        loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUsername(newUser.getUsername());
        loginUserDTO.setPassword(newUser.getPassword());

        editUserDTO = new EditUserDTO();
        editUserDTO.setUsername(newUser.getUsername());
        editUserDTO.setPassword(newUser.getPassword());
    }

    @Test
    void registerUser() throws Exception {
        User registeredUser = userService.addNewUser(newUserDTO);
        assertEquals(expectedUser, registeredUser);
    }

    @Test
    void loginUser() throws UserNotFoundException {
        Optional<User> previouslyRegisteredUser = Optional.of(newUser);
        Mockito.when(
                userRepository.getByUsernameAndPassword(
                    usernameCaptor.capture(), passwordCaptor.capture()
                ))
            .thenReturn(Optional.of(newUser));

        User loggedInUser = userService.login(loginUserDTO);
        assertEquals(previouslyRegisteredUser.get(), loggedInUser);
    }

    @Test
    void loginUserWithWrongPassword() {
        Mockito.when(
                userRepository.getByUsernameAndPassword(
                    usernameCaptor.capture(), passwordCaptor.capture()
                ))
            .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.login(loginUserDTO);
        });
    }

    @Test
    void updateUserPassword() throws UserNotFoundException, SamePasswordException {
        Mockito.when(
                userRepository.getByUsernameAndPassword(
                    usernameCaptor.capture(), passwordCaptor.capture()
                ))
            .thenReturn(Optional.of(newUser));

        editUserDTO.setNewPassword("newPassword");
        User updatedUser = userService.updatePassword(editUserDTO);

        assertEquals(editUserDTO.getNewPassword(), updatedUser.getPassword());
    }

    @Test
    void updateUserDisplayName() throws UserNotFoundException {
        Mockito.when(
                userRepository.getByUsernameAndPassword(
                    usernameCaptor.capture(), passwordCaptor.capture()
                ))
            .thenReturn(Optional.of(newUser));

        editUserDTO.setNewDisplayName("newDisplayName");
        User updatedUser = userService.updateDisplayName(editUserDTO);

        assertEquals(editUserDTO.getNewDisplayName(), updatedUser.getDisplayName());
    }

    @Test
    void updateUserCapital() throws UserNotFoundException, NegativeCapitalChangeException {
        Mockito.when(
                userRepository.getByUsernameAndPassword(
                    usernameCaptor.capture(), passwordCaptor.capture()
                ))
            .thenReturn(Optional.of(newUser));

        editUserDTO.setCapitalChange(500.0);
        User updatedUser = userService.updateCapital(editUserDTO);

        assertEquals(newUser.getCapital(), updatedUser.getCapital());
    }

    @Test
    void updateUserCapitalNegativeValue() {
        Mockito.when(
                userRepository.getByUsernameAndPassword(
                    usernameCaptor.capture(), passwordCaptor.capture()
                ))
            .thenReturn(Optional.of(newUser));

        editUserDTO.setCapitalChange(-500.0);

        assertThrows(NegativeCapitalChangeException.class, () -> {
            userService.updateCapital(editUserDTO);
        });
    }
}
