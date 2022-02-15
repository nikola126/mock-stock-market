package com.stock.backend.services;

import java.util.Objects;
import java.util.Optional;

import com.stock.backend.dtos.EditUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.exceptions.UserExceptions.NegativeCapitalChangeException;
import com.stock.backend.exceptions.UserExceptions.SamePasswordException;
import com.stock.backend.exceptions.UserExceptions.UserAlreadyExistsException;
import com.stock.backend.exceptions.UserExceptions.UserNotFoundException;
import com.stock.backend.models.User;
import com.stock.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(Long id) {
        return userRepository.getById(id);
    }

    public Optional<User> getByUsername(String userName) {
        return userRepository.getByUsername(userName);
    }

    public Optional<User> getByUsernameAndPassword(String userName, String password) {
        return userRepository.getByUsernameAndPassword(userName, password);
    }

    public User login(LoginUserDTO loginUserDTO) throws UserNotFoundException {
        Optional<User> user = getByUsernameAndPassword(loginUserDTO.getUsername(), loginUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            return user.get();
        }
    }

    public User addNewUser(NewUserDTO newUserDTO) throws UserAlreadyExistsException {

        Optional<User> existingUser = getByUsername(newUserDTO.getUsername());

        if (existingUser.isEmpty()) {
            User newUser = new User(newUserDTO.getUsername(), newUserDTO.getPassword(), newUserDTO.getDisplayName(),
                newUserDTO.getCapital());
            userRepository.save(newUser);

            return newUser;
        } else {
            throw new UserAlreadyExistsException("User with this username already exists!");
        }

    }

    public User updatePassword(EditUserDTO editUserDTO) throws SamePasswordException, UserNotFoundException {

        Optional<User> user = getByUsernameAndPassword(editUserDTO.getUsername(), editUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else if (Objects.equals(user.get().getPassword(), editUserDTO.getNewPassword())) {
            throw new SamePasswordException("Password is same as the old one!");
        } else {
            user.get().setPassword(editUserDTO.getNewPassword());
            userRepository.save(user.get());
        }

        return user.get();
    }

    public User updateDisplayName(EditUserDTO editUserDTO) throws UserNotFoundException {
        Optional<User> user = getByUsernameAndPassword(editUserDTO.getUsername(), editUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            user.get().setDisplayName(editUserDTO.getNewDisplayName());
            userRepository.save(user.get());
        }

        return user.get();
    }

    public User updateCapital(EditUserDTO editUserDTO) throws UserNotFoundException, NegativeCapitalChangeException {
        Optional<User> user = getByUsernameAndPassword(editUserDTO.getUsername(), editUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            if (editUserDTO.getCapitalChange() < 0) {
                throw new NegativeCapitalChangeException("Capital can only be added!");
            }
            user.get().setCapital(user.get().getCapital() + editUserDTO.getCapitalChange());
            userRepository.save(user.get());
        }

        return user.get();
    }

}
