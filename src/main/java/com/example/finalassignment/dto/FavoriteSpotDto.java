package com.example.finalassignment.dto;

import com.example.finalassignment.domain.Spot;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * お気に入り一覧画面に表示するためのデータを格納するDTO。
 */
@Data
public class FavoriteSpotDto {

    // ★★★ Spotオブジェクトを格納するフィールドを追加 ★★★
    private Spot spot;

    // ★★★ 登録日時を格納するフィールドを追加 ★★★
    private LocalDateTime createdAt;
}
