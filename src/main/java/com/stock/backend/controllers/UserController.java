package com.stock.backend.controllers;

import com.stock.backend.dtos.EditUserDTO;
import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.UserExceptions.UserAlreadyExistsException;
import com.stock.backend.exceptions.UserExceptions.UserNotFoundException;
import com.stock.backend.models.User;
import com.stock.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
            newUser = userService.addNewUser(newUserDTO);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return newUser;
    }

    @PostMapping(path = "/login")
    public UserDTO login(@RequestBody LoginUserDTO loginUserDTO) {
        try {
            return userService.login(loginUserDTO);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/edit")
    public UserDTO edit(@RequestBody EditUserDTO editUserDTO) {
        User editedUser = userService.getByUsername(editUserDTO.getUsername()).get();
        if (editUserDTO.getPassword() != null) {
            editedUser.setPassword(editUserDTO.getPassword());
        } else if (editUserDTO.getDisplayName() != null) {
            editedUser.setDisplayName(editUserDTO.getDisplayName());
        } else if (editUserDTO.getCapital() != null) {
            editedUser.setCapital(editUserDTO.getCapital());
        }

        return editedUser.mapToDTO();
    }

    @PostMapping(path = "/delete")
    public void delete(@RequestBody UserDTO userDTO) {
        userService.deleteUser(userDTO);
    }

}
