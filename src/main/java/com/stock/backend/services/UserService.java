package com.stock.backend.services;

import java.util.Objects;
import java.util.Optional;

import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.EditUserDTO;
import com.stock.backend.dtos.LoginUserDTO;
import com.stock.backend.dtos.NewUserDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.enums.Actions;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import com.stock.backend.exceptions.InvalidActionException;
import com.stock.backend.exceptions.UserExceptions.InvalidApiTokenException;
import com.stock.backend.exceptions.UserExceptions.NegativeCapitalChangeException;
import com.stock.backend.exceptions.UserExceptions.SamePasswordException;
import com.stock.backend.exceptions.UserExceptions.UserAlreadyExistsException;
import com.stock.backend.exceptions.UserExceptions.UserNotFoundException;
import com.stock.backend.models.Transaction;
import com.stock.backend.models.User;
import com.stock.backend.repositories.TransactionRepository;
import com.stock.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final ApiController apiController;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, TransactionRepository transactionRepository,
                       ApiController apiController) {
        this.apiController = apiController;
        this.transactionRepository = transactionRepository;
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

            // add capital transaction
            Transaction newTransaction = new Transaction();
            newTransaction.setUser(newUser);
            newTransaction.setAction(Actions.ADD);
            newTransaction.setPrice(newUserDTO.getCapital());
            newTransaction.setDate(System.currentTimeMillis());
            transactionRepository.save(newTransaction);

            return newUser;
        } else {
            throw new UserAlreadyExistsException("User with this username already exists!");
        }

    }

    public void deleteUser(LoginUserDTO loginUserDTO) throws UserNotFoundException {
        Optional<User> user = getByUsernameAndPassword(loginUserDTO.getUsername(), loginUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            userRepository.deleteById(user.get().getId());
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

    public User updateApiToken(EditUserDTO editUserDTO) throws UserNotFoundException, InvalidApiTokenException {
        Optional<User> user = getByUsernameAndPassword(editUserDTO.getUsername(), editUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            try {
                QuoteRequestDTO quoteRequestDTO = new QuoteRequestDTO();
                quoteRequestDTO.setSymbol("AAPL");
                quoteRequestDTO.setToken(editUserDTO.getNewApiToken());

                apiController.apiQuote(quoteRequestDTO);
            } catch (ApiException e) {
                throw new InvalidApiTokenException("This API token is invalid!");
            }

            user.get().setApiToken(editUserDTO.getNewApiToken());
            userRepository.save(user.get());
        }
        return user.get();
    }

    public User updateCapital(EditUserDTO editUserDTO)
        throws UserNotFoundException, NegativeCapitalChangeException, InvalidActionException {
        Optional<User> user = getByUsernameAndPassword(editUserDTO.getUsername(), editUserDTO.getPassword());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Wrong credentials provided!");
        } else {
            if (editUserDTO.getCapitalChange() < 0) {
                throw new NegativeCapitalChangeException("Capital can only be added!");
            }
            Double newCapital = user.get().getCapital() + editUserDTO.getCapitalChange();

            // update user capital
            user.get().setCapital(newCapital);
            userRepository.save(user.get());

            // add capital transaction
            Transaction newTransaction = new Transaction();
            newTransaction.setUser(user.get());
            newTransaction.setAction(Actions.ADD);
            newTransaction.setPrice(editUserDTO.getCapitalChange());
            newTransaction.setDate(System.currentTimeMillis());
            transactionRepository.save(newTransaction);
        }

        return user.get();
    }

}
