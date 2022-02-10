package com.stock.backend.repositories;

import java.util.Optional;

import com.stock.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getByUsername(String username);

    Optional<User> getByUsernameAndPassword(String username, String password);
}
