package com.ema.ema_backend.domain.type;

import com.ema.ema_backend.global.exception.ChapterNotFoundException;

public enum ChapterType {
    CHAPTER_1("함수와 모델 (Functions and Models)"),
    CHAPTER_2("극한과 도함수 (Limits and Derivatives)"),
    CHAPTER_3("미분 법칙 (Differentiation Rules)"),
    CHAPTER_4("미분의 응용 (Applications of Differentiation)"),
    CHAPTER_5("적분 (Integrals)"),
    CHAPTER_6("적분의 응용 (Applications of Integration)"),
    CHAPTER_7("적분 기법 (Techniques of Integration)"),
    CHAPTER_8("적분의 추가 응용 (Further Applications of Integration)"),
    CHAPTER_9("미분방정식 (Differential Equations)"),
    CHAPTER_10("매개변수 방정식과 극좌표 (Parametric Equations and Polar Coordinates)"),
    CHAPTER_11("무한 수열과 급수 (Infinite Sequences and Series)"),
    CHAPTER_12("벡터와 공간 기하학 (Vectors and the Geometry of Space)"),
    CHAPTER_13("벡터 함수 (Vector Functions)"),
    CHAPTER_14("편미분 (Partial Derivatives)"),
    CHAPTER_15("다중 적분 (Multiple Integrals)"),
    CHAPTER_16("벡터 미적분학 (Vector Calculus)"),
    CHAPTER_17("2계 미분방정식 (Second-Order Differential Equations)"),
    INITIAL("초기값"),
    INVALID("예외");

    private String chapterName;

    ChapterType(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public static ChapterType getChapterType(String chapterName) {
        for (ChapterType chapterType : values()) {
            if (chapterType.getChapterName().equals(chapterName)) {
                return chapterType;
            }
        }
        return INVALID;
    }

    public static ChapterType fromStringNumber(String number) {
        try {
            return ChapterType.valueOf("CHAPTER_" + number);
        } catch (IllegalArgumentException e) {
            throw new ChapterNotFoundException("해당하는 챕터가 없습니다: " + number);
        }
    }
}