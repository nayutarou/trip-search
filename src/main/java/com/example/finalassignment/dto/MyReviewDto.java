// ===================================================================
// 1-1. 一覧表示用のDTOを新規作成
// ===================================================================
// src/main/java/com/example/finalassignment/dto/MyReviewDto.java

package com.example.finalassignment.dto;

import com.example.finalassignment.domain.Spot;
import com.example.finalassignment.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 「自分の口コミ一覧」ページに表示する情報をまとめたDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewDto {

    private Review review; // 口コミ情報
    private Spot spot;     // 場所の情報
}