package com.example.finalassignment.service;

import com.example.finalassignment.domain.Spot;
import com.example.finalassignment.dto.FavoriteSpotDto;
import com.example.finalassignment.entity.Favorite;
import com.example.finalassignment.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★★★ Transactionalをインポート ★★★

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * お気に入り機能に関するビジネスロジックを担当するサービス。
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final SpotService spotService;

    public FavoriteService(FavoriteRepository favoriteRepository, SpotService spotService) {
        this.favoriteRepository = favoriteRepository;
        this.spotService = spotService;
    }

    /**
     * 指定された場所がユーザーのお気に入りかどうかをチェックします。
     * @param placeId 場所のID
     * @param userId ユーザーのID
     * @return お気に入りならtrue
     */
    // 参照系なのでトランザクションは任意ですが、つけておくと一貫性が保たれます。
    @Transactional(readOnly = true)
    public boolean isFavorite(String placeId, Long userId) {
        return favoriteRepository.existsByUserIdAndPlaceId(userId, placeId);
    }

    /**
     * お気に入りに追加します。
     * @param placeId 場所のID
     * @param userId ユーザーのID
     */
    @Transactional
    public void addFavorite(String placeId, Long userId) {
        // すでに存在しないかチェック（念のため）
        if (!isFavorite(placeId, userId)) {
            Favorite favorite = new Favorite(userId, placeId);
            favoriteRepository.save(favorite);
        }
    }

    /**
     * お気に入りから削除します。
     * ★★★ カスタムの削除メソッドを呼び出すため、@Transactionalが必須です ★★★
     * これがないと、Repositoryのメソッドを実行できません。
     * @param placeId 場所のID
     * @param userId ユーザーのID
     */
    @Transactional
    public void removeFavorite(String placeId, Long userId) {
        favoriteRepository.deleteByUserIdAndPlaceId(userId, placeId);
    }

    /**
     * 指定されたユーザーのお気に入りリストを、詳細情報付きで取得します。
     * @param userId ユーザーID
     * @return お気に入り場所の詳細情報リスト
     */
    @Transactional(readOnly = true)
    public List<FavoriteSpotDto> findFavoriteDetailsByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.parallelStream()
                .map(favorite -> {
                    Spot spot = spotService.findDetailsByPlaceId(favorite.getPlaceId());
                    if (spot != null) {
                        FavoriteSpotDto dto = new FavoriteSpotDto();
                        dto.setSpot(spot);
                        dto.setCreatedAt(favorite.getCreatedAt());
                        return dto;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}