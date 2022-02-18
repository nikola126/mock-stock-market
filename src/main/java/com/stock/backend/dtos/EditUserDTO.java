package com.stock.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditUserDTO {
    private String username;
    private String password;
    private String newPassword;
    private String newDisplayName;
    private String newApiToken;
    private Double capitalChange;
}
