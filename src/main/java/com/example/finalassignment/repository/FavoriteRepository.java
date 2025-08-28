// FavoriteRepository.java

package com.example.finalassignment.repository;

import com.example.finalassignment.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Favoriteエンティティのデータベース操作を担当するリポジトリ。
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * ユーザーIDと場所IDに一致するお気に入りが存在するかどうかを確認します。
     * Spring Data JPAがメソッド名から自動的にクエリを生成します。
     * (SELECT COUNT(*) FROM favoritePlaces WHERE user_id = ? AND place_id = ?)
     *
     * @param userId ユーザーID
     * @param placeId 場所のID
     * @return 存在すればtrue
     */
    boolean existsByUserIdAndPlaceId(Long userId, String placeId);

    /**
     * 指定されたユーザーIDのお気に入りリストを取得します。
     * (SELECT * FROM favoritePlaces WHERE user_id = ?)
     *
     * @param userId ユーザーID
     * @return お気に入りエンティティのリスト
     */
    List<Favorite> findByUserId(Long userId);

    /**
     * ユーザーIDと場所IDに一致するお気に入りを削除します。
     * ★★★ 重要 ★★★
     * JpaRepositoryの規約外の削除メソッドなので、呼び出し元のService側で@Transactionalが必要です。
     *
     * @param userId ユーザーID
     * @param placeId 場所のID
     */
    void deleteByUserIdAndPlaceId(Long userId, String placeId);
}