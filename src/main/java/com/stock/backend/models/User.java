package com.stock.backend.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.stock.backend.dtos.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String displayName;

    @Column
    private Double capital;

    public User(String username, String password, String displayName, Double capital) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.capital = capital;
    }

    public UserDTO mapToDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(this.getUsername());
        userDTO.setId(this.getId());
        userDTO.setDisplayName(this.getDisplayName());
        userDTO.setCapital(this.getCapital());

        return userDTO;
    }
}
