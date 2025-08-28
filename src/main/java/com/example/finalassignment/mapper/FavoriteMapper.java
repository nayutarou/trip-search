package com.example.finalassignment.mapper;

import com.example.finalassignment.dto.FavoriteDto;
import com.example.finalassignment.entity.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    /**
     * FavoriteエンティティをFavoriteDtoに変換します。
     * @param favorite データベースから取得したエンティティ
     * @return 画面表示用のDTO
     */
    public FavoriteDto toDto(Favorite favorite) {
        if (favorite == null) {
            return null;
        }

        FavoriteDto dto = new FavoriteDto();
        dto.setFavoritePlaceId(favorite.getFavoritePlaceId());
        dto.setUserId(favorite.getUserId());
        dto.setPlaceId(favorite.getPlaceId());

        return dto;
    }

    /**
     * FavoriteDtoをFavoriteエンティティに変換します。
     * @param dto 画面などから受け取ったDTO
     * @return データベース保存用のエンティティ
     */
    public Favorite toEntity(FavoriteDto dto) {
        if (dto == null) {
            return null;
        }

        Favorite favorite = new Favorite();
        // IDは自動採番なので、ここではセットしない
        favorite.setUserId(dto.getUserId());
        favorite.setPlaceId(dto.getPlaceId());

        return favorite;
    }
}
