package com.stock.backend.services;

import java.util.Objects;
import java.util.Optional;

import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.dtos.UpdateUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.UserDTO;
import com.stock.backend.exceptions.SamePasswordException;
import com.stock.backend.exceptions.UserAlreadyExistsException;
import com.stock.backend.exceptions.UserNotFoundException;
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

    public UserDTO login(LoginUserDTO loginUserDTO) throws UserNotFoundException {
        Optional<User> user = getByUsernameAndPassword(loginUserDTO.getUsername(), loginUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            UserDTO userDTO = new UserDTO();
            userDTO.mapFromUser(user.get());
            return userDTO;
        }
    }

    public UserDTO addNewUser(NewUserDTO newUserDTO) throws UserAlreadyExistsException {

        Optional<User> existingUser = getByUsername(newUserDTO.getUsername());

        if (existingUser.isEmpty()) {
            User newUser = new User(newUserDTO.getUsername(), newUserDTO.getPassword(), newUserDTO.getDisplayName());
            userRepository.save(newUser);

            UserDTO userDTO = new UserDTO();
            userDTO.mapFromUser(newUser);
            return userDTO;
        } else {
            throw new UserAlreadyExistsException("User with this username already exists!");
        }

    }

    public User updatePassword(UpdateUserDTO updateUserDTO, String newPassword)
        throws SamePasswordException, UserNotFoundException {

        Optional<User> user = getByUsernameAndPassword(updateUserDTO.getUsername(), updateUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else if (Objects.equals(user.get().getPassword(), newPassword)) {
            throw new SamePasswordException("Password is same as the old one!");
        } else {
            user.get().setPassword(newPassword);
        }

        return user.get();
    }

    public User updateDisplayName(UpdateUserDTO updateUserDTO, String newDisplayName)
        throws UserNotFoundException {
        Optional<User> user = getByUsernameAndPassword(updateUserDTO.getUsername(), updateUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            user.get().setDisplayName(newDisplayName);
        }

        return user.get();
    }
}
