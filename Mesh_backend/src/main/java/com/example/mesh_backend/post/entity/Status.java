package com.example.mesh_backend.post.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공고 상태 (모집 중, 모집 완료, 활동 중, 활동 완료)")
public enum Status {
    모집중("모집 중"),
    모집완료("모집 완료"),
    활동중("활동 중"),
    활동완료("활동 완료");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.displayName.equalsIgnoreCase(value.trim()) || status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status value: " + value);
    }
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
