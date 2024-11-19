package com.example.mesh_backend.login.dto.request;

import lombok.Data;

@Data
public class KakaoTokenRequest {
    private String refreshToken;
}