package com.example.mesh_backend.mypage.dto.request;

import com.example.mesh_backend.mypage.entity.Scale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardRequest {
    private String projectName;
    private String part;
    private String result;
    private Scale scale;
}