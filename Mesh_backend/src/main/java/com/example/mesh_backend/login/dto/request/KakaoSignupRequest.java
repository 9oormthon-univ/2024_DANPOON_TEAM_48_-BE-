package com.example.mesh_backend.login.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoSignupRequest {//회원가입 시 받는 추가적 데이터

    private String nickname;
    private String major;

}