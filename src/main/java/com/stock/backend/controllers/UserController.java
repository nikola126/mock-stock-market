package com.stock.backend.controllers;

import com.stock.backend.dtos.EditUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.InvalidActionException;
import com.stock.backend.exceptions.UserExceptions.InvalidApiTokenException;
import com.stock.backend.exceptions.UserExceptions.NegativeCapitalChangeException;
import com.stock.backend.exceptions.UserExceptions.SamePasswordException;
import com.stock.backend.exceptions.UserExceptions.UserAlreadyExistsException;
import com.stock.backend.exceptions.UserExceptions.UserNotFoundException;
import com.stock.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public UserDTO addUser(@RequestBody NewUserDTO newUserDTO) {
        UserDTO newUser;

        try {
            newUser = userService.addNewUser(newUserDTO).mapToDTO();
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return newUser;
    }

    @PostMapping(path = "/login")
    public UserDTO login(@RequestBody LoginUserDTO loginUserDTO) {
        try {
            return userService.login(loginUserDTO).mapToDTO();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping(path = "/delete")
    public void delete(@RequestBody LoginUserDTO loginUserDTO) {
        try {
            userService.deleteUser(loginUserDTO);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/edit")
    public UserDTO edit(@RequestBody EditUserDTO editUserDTO) throws UserNotFoundException {
        if (editUserDTO.getNewPassword() != null) {
            try {
                return userService.updatePassword(editUserDTO).mapToDTO();
            } catch (SamePasswordException | UserNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else if (editUserDTO.getNewDisplayName() != null) {
            try {
                return userService.updateDisplayName(editUserDTO).mapToDTO();
            } catch (UserNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else if (editUserDTO.getCapitalChange() != null) {
            try {
                return userService.updateCapital(editUserDTO).mapToDTO();
            } catch (UserNotFoundException | NegativeCapitalChangeException | InvalidActionException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else if (editUserDTO.getNewApiToken() != null) {
            try {
                return userService.updateApiToken(editUserDTO).mapToDTO();
            } catch (InvalidApiTokenException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }
    }

}
