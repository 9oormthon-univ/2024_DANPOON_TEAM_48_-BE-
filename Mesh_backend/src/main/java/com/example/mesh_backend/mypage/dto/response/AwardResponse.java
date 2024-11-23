package com.example.mesh_backend.mypage.dto.response;

import com.example.mesh_backend.mypage.entity.Scale;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AwardResponse {
    private String projectName;
    private List<String> certificateUrls; // 업로드된 증명서 URL 리스트
    private Scale scale;
}