// ===================================================================
// 1-1. 評価と件数を格納するDTOを新規作成
// ===================================================================
// src/main/java/com/example/finalassignment/dto/AppRatingDto.java

package com.example.finalassignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AppRatingDto {
    private Double averageRating; // 平均評価
    private Long reviewCount;     // 口コミ件数
    // ★★★ JPQLが呼び出すためのコンストラクタを手動で定義 ★★★
    public AppRatingDto(Double averageRating, Long reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }
}