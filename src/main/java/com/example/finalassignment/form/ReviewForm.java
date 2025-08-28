// src/main/java/com/example/finalassignment/dto/ReviewForm.java

package com.example.finalassignment.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile; // ★★★ MultipartFileをインポート ★★★
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 口コミ投稿フォームからのデータを受け取るためのDTO
 */
@Data
public class ReviewForm {

    // ★★★ 編集対象のIDを保持するために追加 ★★★
    private Long reviewId;

    private String placeId; // 口コミ対象の場所ID (ユーザーが検索して選択)

    private BigDecimal rating; // 評価点

    private String comment; // コメント

    private LocalDate visitedAt; // 訪問日

    private MultipartFile imageFile;

    // ★★★ 編集画面で既存の画像パスを表示するために追加 ★★★
    private String existingImagePath;
}
