package com.example.mesh_backend.login.entity;

public enum SubCategoryName {
    일반_기획(MainCategoryName.기획),
    서비스_기획(MainCategoryName.기획),
    PM(MainCategoryName.기획),
    데이터_분석(MainCategoryName.기획),
    UI_디자인(MainCategoryName.디자인),
    UX_디자인(MainCategoryName.디자인),
    웹_디자인(MainCategoryName.디자인),
    그래픽_디자인(MainCategoryName.디자인),
    브랜드_디자인(MainCategoryName.디자인),
    FIGMA(MainCategoryName.디자인),
    Photoshop(MainCategoryName.디자인),
    Illustrator(MainCategoryName.디자인),

    Java(MainCategoryName.백엔드),
    Python(MainCategoryName.백엔드),
    JavaScript(MainCategoryName.백엔드),
    Ruby(MainCategoryName.백엔드),
    PHP(MainCategoryName.백엔드),
    Spring(MainCategoryName.백엔드),
    Django(MainCategoryName.백엔드),
    Express(MainCategoryName.백엔드),
    React(MainCategoryName.프론트엔드),
    Vue(MainCategoryName.프론트엔드),
    Angular(MainCategoryName.프론트엔드),
    Svelte(MainCategoryName.프론트엔드),
    JQuery(MainCategoryName.프론트엔드),
    Ember(MainCategoryName.프론트엔드),
    Backbone(MainCategoryName.프론트엔드),
    Semantic_UI(MainCategoryName.프론트엔드),
    Foundation(MainCategoryName.프론트엔드),
    Preact(MainCategoryName.프론트엔드),
    Notion(MainCategoryName.협업),
    Slack(MainCategoryName.협업),
    Discord(MainCategoryName.협업);

    private final MainCategoryName mainCategory;

    SubCategoryName(MainCategoryName mainCategory) {
        this.mainCategory = mainCategory;
    }

    public MainCategoryName getMainCategory() {
        return mainCategory;
    }
}