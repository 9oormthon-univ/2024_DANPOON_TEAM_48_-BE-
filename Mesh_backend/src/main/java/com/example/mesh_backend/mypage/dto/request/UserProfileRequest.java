package com.example.mesh_backend.mypage.dto.request;

import com.example.mesh_backend.login.entity.MainCategoryName;
import com.example.mesh_backend.login.entity.SubCategoryName;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileRequest {

    private String major;
    private String nickname;
    private String content;
    private String portfolio;

    private List<MainCategoryName> mainCategories;
    private List<SubCategoryName> subCategories;

    private List<ToolRequest> tools;
}