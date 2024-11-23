package com.example.mesh_backend.mypage.dto.request;

import com.example.mesh_backend.mypage.entity.Scale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardRequest {
    private String projectName; // 프로젝트 설명
    private List<String> certificateUrl;
    private Scale scale;
}