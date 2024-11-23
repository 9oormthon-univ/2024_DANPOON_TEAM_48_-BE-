package com.example.mesh_backend.mypage.entity;

import com.example.mesh_backend.login.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id")
    private Long awardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "project_name")
    private String projectName;  //대회 정보 입력

    @ElementCollection // 다중 URL 저장을 위한 필드
    @CollectionTable(name = "award_certificate_urls", joinColumns = @JoinColumn(name = "award_id"))
    @Column(name = "certificate_url") //상장과 같은 첨부파일관련
    private List<String> certificateUrls = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    @Column
    private Scale scale;

    public void addCertificateUrl(String url) {
        this.certificateUrls.add(url);
    }
}
