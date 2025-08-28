package com.example.finalassignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 場所検索APIのレスポンスとして返す、予測変換用のDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearchResultDto {

    private String placeId; // 場所のID
    private String name;    // 場所の名前
}

