package com.stock.backend.repositoryTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.stock.backend.models.User;
import com.stock.backend.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void saveSingleUser() {
        User newUser = userRepository.save(user);

        assertEquals(newUser.getId(), 1L);
    }

    @Test
    void saveMultipleUsers() {
        User user2 = new User();
        user2.setUsername("testUsername2");
        User user3 = new User();
        user3.setUsername("testUsername3");

        List<User> usersList = new ArrayList<>();
        usersList.add(user);
        usersList.add(user2);
        usersList.add(user3);

        userRepository.saveAll(usersList);
        List<User> registeredUsers = userRepository.findAll();

        assertEquals(3, registeredUsers.size());
    }
}
