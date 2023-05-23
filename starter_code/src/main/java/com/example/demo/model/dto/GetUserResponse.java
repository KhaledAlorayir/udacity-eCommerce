package com.example.demo.model.dto;

import com.example.demo.model.persistence.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetUserResponse {
    private Long id;
    private String username;

    public GetUserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

}