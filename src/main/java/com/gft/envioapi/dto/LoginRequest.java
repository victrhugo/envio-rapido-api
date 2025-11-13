package com.gft.envioapi.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String senha;
}
