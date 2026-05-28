package com.example.gymcrm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Auth {
    private String username;
    private String password;

    public Auth() {
    }

    public Auth(String username, String password) {
        this.username = username;
        this.password = password;
    }

}