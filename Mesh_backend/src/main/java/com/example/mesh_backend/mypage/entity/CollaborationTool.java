package com.example.mesh_backend.mypage.entity;

import java.util.Arrays;

public enum CollaborationTool { //숙련도 별점 없음 -> 예외처리를 위해 enum으로 정의    NOTION,
    Notion,
    Slack,
    Discord;

    public static boolean isCollaborationTool(String toolName) {
        return Arrays.stream(CollaborationTool.values())
                .anyMatch(tool -> tool.name().equalsIgnoreCase(toolName));
    }
}