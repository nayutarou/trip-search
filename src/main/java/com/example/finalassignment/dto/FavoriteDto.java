package com.example.finalassignment.dto;

import lombok.Data;

/**
 * お気に入り情報の基本的なデータ転送オブジェクト(DTO)。
 * FavoriteエンティティのID情報をシンプルにやり取りするために使います。
 */
@Data
public class FavoriteDto {

    private Long favoritePlaceId;
    private Long userId;
    private String placeId;

}
