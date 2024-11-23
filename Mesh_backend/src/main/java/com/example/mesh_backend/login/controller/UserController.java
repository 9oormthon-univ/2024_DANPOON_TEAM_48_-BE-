package com.example.mesh_backend.login.controller;

import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.common.utils.S3Uploader;
import com.example.mesh_backend.login.dto.request.KakaoSignupRequest;
import com.example.mesh_backend.login.dto.request.KakaoTokenRequest;
import com.example.mesh_backend.login.dto.response.UserIdResponse;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import com.example.mesh_backend.login.security.CustomUserDetails;
import com.example.mesh_backend.login.service.TokenService;
import com.example.mesh_backend.login.service.UserService;
import com.example.mesh_backend.message.BasicResponse;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
@Tag(name = "카카오 Oauth", description = "카카오 Oauth API")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${kakao.admin-key}")
    private String adminKey;


    //0. 카카오에서 Accesstoeken받기
    @PostMapping("/kakao/token")
    @Operation(summary = "인가 코드로 액세스 토큰 발급(백앤드용)", description = "카카오 인가 코드로 액세스 토큰을 발급받는 API(백앤드용)")
    public ResponseEntity<BasicResponse<String>> getKakaoAccessToken(
            @RequestParam(name = "code") String code) {

        try {
            String url = "https://kauth.kakao.com/oauth/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);
            params.add("client_secret", clientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // 응답에서 액세스 토큰 추출
            String accessToken = extractAccessToken(response.getBody());

            return ResponseEntity.ok(BasicResponse.ofSuccess(accessToken));

        } catch (Exception e) {
            log.error("액세스 토큰 발급 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BasicResponse.ofError(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    //1. 회원가입
    @PostMapping("/signup/kakao")
    @Operation(summary = "카카오 회원가입", description = "카카오 인증 후 닉네임과 전공을 추가로 입력하여 회원가입 완료 API")
    public ResponseEntity<BasicResponse<UserIdResponse>> kakaoSignup(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody KakaoSignupRequest request) {
        try {
            // 1. AccessToken에서 Bearer 제거
            String accessToken = extractBearerToken(accessTokenHeader);

            // 2. 카카오 사용자 정보 가져오기
            User kakaoUser = userService.getKakaoUser(accessToken);

            // 3. 이메일 중복 체크
            if (userService.findByEmail(kakaoUser.getEmail()) != null) {
                return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.EMAIL_ALREADY_EXISTS));
            }

            // 4. 새로운 사용자 생성 및 저장
            User user = new User();
            user.setEmail(kakaoUser.getEmail());
            user.setKakaoId(kakaoUser.getKakaoId());

            // 5. 카카오 프로필 이미지 S3에 업로드
            if (kakaoUser.getProfileImageUrl() != null) {
                File imageFile = downloadImageFromUrl(kakaoUser.getProfileImageUrl());
                String s3ImageUrl = s3Uploader.upload(imageFile, "profile-images");
                user.setProfileImageUrl(s3ImageUrl);
                imageFile.delete();
            }

            // 6. 닉네임 중복 검증
            userService.validateNickname(request.getNickname());
            user.setNickname(request.getNickname());

            // 7. 전공 설정
            user.setMajor(request.getMajor());

            // 8. 사용자 저장
            user = userService.signup(user);

            // 9. RefreshToken 생성 및 저장
            String refreshToken = tokenService.createRefreshToken(user);
            tokenService.saveRefreshToken(user, refreshToken);

            // 10. 응답 생성
            UserIdResponse responseData = new UserIdResponse(user.getUserId());
            return ResponseEntity.ok(BasicResponse.ofSuccess(responseData));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.DUPLICATE_NICKNAME));
        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BasicResponse.ofError(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }



    //2. 로그인
    @PostMapping("/login/kakao")
    @Operation(summary = "로그인", description = "엑세스 토큰을 사용하여 로그인 진행")
    public ResponseEntity<BasicResponse<String>> kakaoLogin(
            @RequestHeader("Authorization") String accessTokenHeader) {
        try {
            String accessToken = extractBearerToken(accessTokenHeader);
            User kakaoUser = userService.getKakaoUser(accessToken);
            User user = userService.findByEmail(kakaoUser.getEmail());

            if (user == null) {
                return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_NOT_FOUND));
            }

            // RefreshToken 처리
            String refreshToken = tokenService.getRefreshToken(user);
            if (refreshToken == null) {
                refreshToken = tokenService.createRefreshToken(user);
                tokenService.saveRefreshToken(user, refreshToken);
            }

            // 새로운 AccessToken 생성
            String newAccessToken = tokenService.renewAccessToken(refreshToken);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + newAccessToken + ", Refresh " + refreshToken);

            return ResponseEntity.ok().headers(headers).body(BasicResponse.ofSuccess("로그인 성공"));

        } catch (Exception e) {
            log.error("오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BasicResponse.ofError(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }


    //3. 로그아웃
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<BasicResponse<String>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7); // "Bearer " 부분 제거
            userService.logout(accessToken);
        }

        BasicResponse<String> response = BasicResponse.ofSuccess("로그아웃 성공");
        return ResponseEntity.ok(response);
    }


    //4.회원탈퇴
    @Operation(summary = "회원탈퇴", description = "카카오+DB에서 회원탈퇴 API")
    @PostMapping("/withdraw")
    public ResponseEntity<BasicResponse<String>> withdraw(HttpServletRequest request) {

        String userIdHeader = request.getHeader("userId"); // userId를 통해 kakaoId를 가지고 오도록

        String authorizationHeader = "KakaoAK " + adminKey;

        if (userIdHeader != null) {
            Long userId = Long.valueOf(userIdHeader);

            //유저 정보 조회 (=> userId를 통해 kakaoId를 가져옴)
            Optional<User> optionalUser = userRepository.findByUserId(userId);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Long kakaoId = user.getKakaoId();

                userService.withdraw(authorizationHeader, userId);

                BasicResponse<String> response = BasicResponse.ofSuccess("회원탈퇴 성공");
                return ResponseEntity.ok(response);

            }  else {
                return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_NOT_FOUND));
            }
        } else {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_ID_REQUIRED));
        }
    }


    //5. refreshToken으로 accessToken 재발급 -> 백엔드 개발할 때 사용
    @Operation(summary = "accesstoken 재발급", description = "백엔드에서 개발할 때 사용하는 API")
    @PostMapping("/user/refresh")
    public ResponseEntity<BasicResponse<String>> refreshAccessToken(@RequestBody KakaoTokenRequest kakaoTokenRequest) {
        String refreshToken = kakaoTokenRequest.getRefreshToken();
        String newAccessToken = tokenService.renewAccessToken(refreshToken);

        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BasicResponse.ofError(ErrorCode.INVALID_TOKEN));
        }
        BasicResponse<String> response = BasicResponse.ofSuccess("새로운 AccessToken: " + newAccessToken);
        return ResponseEntity.ok(response);
    }

    private File downloadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        File file = File.createTempFile("temp", ".jpg");
        try (InputStream in = url.openStream(); FileOutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    private String extractBearerToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("유효하지 않은 Authorization 헤더 형식입니다.");
    }

    private String extractAccessToken(String responseBody) {
        JsonObject jsonResponse = new JsonParser().parse(responseBody).getAsJsonObject();
        return jsonResponse.get("access_token").getAsString();
    }

}