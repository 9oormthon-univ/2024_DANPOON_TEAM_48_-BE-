package com.example.mesh_backend.mypage.dto.request;

import com.example.mesh_backend.mypage.entity.Scale;
import lombok.Data;

@Data
public class AwardRequest {
    private String projectName;
    private String part;
    private String result;
    private Scale scale;
}