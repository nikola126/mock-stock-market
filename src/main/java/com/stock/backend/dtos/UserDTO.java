package com.stock.backend.dtos;

import com.stock.backend.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String displayName;
    private Long id;

    public void mapFromUser(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.id = user.getId();
    }
}
