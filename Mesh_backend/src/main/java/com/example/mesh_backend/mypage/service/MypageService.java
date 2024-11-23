package com.example.mesh_backend.mypage.service;

import com.example.mesh_backend.login.entity.*;
import com.example.mesh_backend.login.repository.UserRepository;
import com.example.mesh_backend.mypage.dto.request.AwardRequest;
import com.example.mesh_backend.mypage.dto.request.CareerRequest;
import com.example.mesh_backend.mypage.dto.request.ToolRequest;
import com.example.mesh_backend.mypage.dto.request.UserProfileRequest;
import com.example.mesh_backend.mypage.dto.response.UserProfileResponse;
import com.example.mesh_backend.mypage.entity.Award;
import com.example.mesh_backend.mypage.entity.Career;
import com.example.mesh_backend.mypage.entity.CollaborationTool;
import com.example.mesh_backend.mypage.entity.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserProfile(User user, UserProfileRequest request) {

        //1. 닉네임 업데이트
        if (request.getNickname() != null && !user.getNickname().equals(request.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("중복된 닉네임입니다.");
            }
            user.setNickname(request.getNickname());
        }

        //2. 전공 업데이트
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }

        //3. 한 줄 자기소개 업데이트
        if (request.getContent() != null) {
            user.setContent(request.getContent());
        }

        //4. 포트폴리오 링크 업데이트
        if (request.getPortfolio() != null) {
            user.setPortfolio(request.getPortfolio());
        }

        //5. 서브카테고리와 메인카테고리 업데이트
        if (request.getSubCategories() != null) {
            List<Subcategory> subCategories = request.getSubCategories().stream()
                    .map(subCategoryEnum -> {
                        MainCategoryName mainCategoryEnum = subCategoryEnum.getMainCategory();

                        Maincategory mainCategory = user.getMaincategories().stream()
                                .filter(existingMainCategory -> existingMainCategory.getMaincategoryName() == mainCategoryEnum)
                                .findFirst()
                                .orElseGet(() -> {
                                    Maincategory newMainCategory = new Maincategory();
                                    newMainCategory.setUser(user);
                                    newMainCategory.setMaincategoryName(mainCategoryEnum);
                                    return newMainCategory;
                                });

                        Subcategory subcategory = new Subcategory();
                        subcategory.setSubcategoryName(subCategoryEnum);
                        subcategory.setMaincategory(mainCategory);
                        return subcategory;
                    })
                    .collect(Collectors.toList());

            List<Maincategory> mainCategories = subCategories.stream()
                    .collect(Collectors.groupingBy(Subcategory::getMaincategory))
                    .entrySet().stream()
                    .map(entry -> {
                        Maincategory mainCategory = entry.getKey();
                        mainCategory.setSubcategories(entry.getValue());
                        return mainCategory;
                    })
                    .collect(Collectors.toList());
            user.setMaincategories(mainCategories);
        }

//        //6. 수상 경력 업데이트
//        if (request.getAwards() != null) {
//            List<Award> awards = request.getAwards().stream()
//                    .map(awardRequest -> {
//                        Award award = new Award();
//                        award.setProjectName(awardRequest.getProjectName());
//                        award.setCertificateUrl(award.getCertificateUrl());
//                        award.setScale(awardRequest.getScale());
//                        award.setUser(user);
//                        return award;
//                    })
//                    .collect(Collectors.toList());
//            user.setAwards(awards);
//        }
//
//        //7. 경력 업데이트
//        if (request.getCareers() != null) {
//            List<Career> careers = request.getCareers().stream()
//                    .map(careerRequest -> {
//                        Career career = new Career();
//                        career.setDuration(careerRequest.getDuration());
//                        career.setCareerContent(careerRequest.getCareerContent());
//                        career.setUser(user);
//                        return career;
//                    })
//                    .collect(Collectors.toList());
//            user.setCareers(careers);
//        }

        //8. 기술 스택 업데이트
        if (request.getTools() != null) {
            List<Tool> tools = request.getTools().stream()
                    .map(toolRequest -> {
                        String toolName = toolRequest.getToolName();
                        Tool tool = new Tool();
                        tool.setToolName(toolName);
                        tool.setUser(user);

                        if (CollaborationTool.isCollaborationTool(toolName)) {
                            tool.setProficiency(null);
                        } else {
                            tool.setProficiency(toolRequest.getProficiency());
                        }
                        return tool;
                    })
                    .collect(Collectors.toList());
            user.setTools(tools);
        }

        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserProfileResponse(
                user.getUserId(),
                user.getNickname(),
                user.getMeshScore(),
                user.getProfileImageUrl(),
                user.getMajor(),
                user.getContent(),
                user.getPortfolio(),
                user.getMaincategories().stream()
                        .flatMap(mainCategory -> mainCategory.getSubcategories().stream())
                        .map(Subcategory::getSubcategoryName)
                        .collect(Collectors.toList()),
                user.getAwards().stream()
                        .map(award -> new AwardRequest(
                                award.getProjectName(),
                                award.getCertificateUrls(),
                                award.getScale()))
                        .collect(Collectors.toList()),
                user.getCareers().stream()
                        .map(career -> new CareerRequest(
                                career.getDuration(),
                                career.getCareerContent()))
                        .collect(Collectors.toList()),
                user.getTools().stream()
                        .map(tool -> new ToolRequest(
                                tool.getToolName(),
                                tool.getProficiency()))
                        .collect(Collectors.toList())
        );
    }
}
