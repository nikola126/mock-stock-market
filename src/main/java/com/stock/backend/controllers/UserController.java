package com.stock.backend.controllers;

import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.UserAlreadyExistsException;
import com.stock.backend.exceptions.UserNotFoundException;
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

}
