package com.example.mesh_backend.mypage.dto.response;
import com.example.mesh_backend.login.entity.SubCategoryName;
import com.example.mesh_backend.mypage.dto.request.AwardRequest;
import com.example.mesh_backend.mypage.dto.request.CareerRequest;
import com.example.mesh_backend.mypage.dto.request.ToolRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileLessResponse {
    private Long userId;
    private String nickname;
    private Long meshScore;
    private String profileImageUrl;
    private String major;
    private String content;
    private String portfolio;

    private List<SubCategoryName> subCategories;
    private List<ToolRequest> tools;
}
