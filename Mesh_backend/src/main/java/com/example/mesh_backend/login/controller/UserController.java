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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

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


    @Value("${kakao.admin-key}")
    private String adminKey;

    //1. 회원가입
    @PostMapping("/signup/kakao")
    @Operation(summary = "회원가입", description = "최초 인가 코드를 사용하여 카카오 인증 후 회원가입 완료 API")
    public ResponseEntity<BasicResponse<UserIdResponse>> kakaoSignup(
            @RequestParam(name = "code") String code) {

        try {
            String accessToken = userService.getKakaoAccessToken(code);
            User kakaoUser = userService.getKakaoUser(accessToken);

            if (userService.findByEmail(kakaoUser.getEmail()) != null) {
                return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.EMAIL_ALREADY_EXISTS));
            }


            User user = new User();
            user.setEmail(kakaoUser.getEmail());
            user.setKakaoId(kakaoUser.getKakaoId());

            //카카오 프로필 이미지 s3에 업로드
            if (kakaoUser.getProfileImageUrl() != null) {
                File imageFile = downloadImageFromUrl(kakaoUser.getProfileImageUrl());
                String s3ImageUrl = s3Uploader.upload(imageFile, "profile-images");
                user.setProfileImageUrl(s3ImageUrl);
                imageFile.delete();
            }

            user = userService.signup(user);

            UserIdResponse responseData = new UserIdResponse(user.getUserId());
            return ResponseEntity.ok(BasicResponse.ofSuccess(responseData));

        } catch (CustomException e) {
            log.error("CustomException 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BasicResponse.ofError(e.getErrorCode()));
        } catch (IOException e) {
            log.error("I/O 예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.IO_ERROR));
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }



    @PostMapping("/signup/kakao/step1")
    @Operation(summary = "회원가입_1", description = "추가적인 프로필 이미지, 닉네임, 전공을 작성하는 API")
    public ResponseEntity<BasicResponse<String>> step1(
            @RequestParam(name = "userId") Long userId,
            @RequestBody KakaoSignupRequest request) {

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_NOT_FOUND));
        }

        try {
            userService.validateNickname(request.getNickname());
            user.setNickname(request.getNickname());
            user.setMajor(request.getMajor());

            userService.updateUser(user);

            String refreshToken = tokenService.createRefreshToken(user);
            tokenService.saveRefreshToken(user, refreshToken);

            return ResponseEntity.ok(BasicResponse.ofSuccess("최종 회원가입 완료"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.DUPLICATE_NICKNAME));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }


    //2. 로그인
    @GetMapping("/login/kakao")
    @Operation(summary = "로그인", description = "로그인을 진행하는 API")
    public ResponseEntity<BasicResponse<String>> kakaoLogin(
            @RequestParam(name = "code") String code) {

        try {
            String accessToken = userService.getKakaoAccessToken(code);
            User kakaoUser = userService.getKakaoUser(accessToken);
            User user = userService.findByEmail(kakaoUser.getEmail());

            if (user == null) {
                return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_NOT_FOUND));
            }

            String refreshToken = tokenService.getRefreshToken(user);

            if (refreshToken == null) {
                refreshToken = tokenService.createRefreshToken(user);
                tokenService.saveRefreshToken(user, refreshToken); // DB에 RefreshToken 저장
            }

            String newAccessToken = tokenService.renewAccessToken(refreshToken);

            CustomUserDetails userDetails = new CustomUserDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpHeaders headers = new HttpHeaders();
            //헤더에 refresh와 access 함께 보내줌
            headers.set("Authorization", "Bearer " + newAccessToken + ", Refresh " + refreshToken);

            BasicResponse<String> response = BasicResponse.ofSuccess("로그인 성공");
            return ResponseEntity.ok().headers(headers).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.INTERNAL_SERVER_ERROR));
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

}