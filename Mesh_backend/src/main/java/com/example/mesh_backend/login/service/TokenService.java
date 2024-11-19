package com.example.mesh_backend.login.service;

import com.example.mesh_backend.login.entity.Refreshtoken;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.RefreshtokenRepository;
import com.example.mesh_backend.login.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshtokenRepository refreshtokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    // AccessToken 생성
    public String createAccessToken(User user) { //AccessToken을 JWT형식으로 생성함
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 3600000)) //하루
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600000)) //1주일
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // RefreshToken 저장
    public void saveRefreshToken(User user, String refreshToken) {

        Refreshtoken token = new Refreshtoken();

        token.setRefreshToken(refreshToken);
        token.setUser(user);
        refreshtokenRepository.save(token);
    }

    //RefreshToken 이용하여 AccessToken 재발급
    public String renewAccessToken(String refreshToken) {

        Refreshtoken token = refreshtokenRepository.findByRefreshToken(refreshToken).orElse(null);

        if (token == null) {
            return null;
        }
        User user = token.getUser();
        return createAccessToken(user);
    }

    // RefreshToken 조회
    public String getRefreshToken(User user) {
        Refreshtoken refreshToken = refreshtokenRepository.findByUser(user);

        if (refreshToken != null) {
            return refreshToken.getRefreshToken();
        }
        return null;
    }

    // AccessToken을 이용하여 유저 정보 조회
    public User getUserFromAccessToken(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody();

            String userId = claims.getSubject();

            return userRepository.findByUserId(Long.valueOf(userId))
                    .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다. : " + userId));

        } catch (SignatureException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

    }
}
