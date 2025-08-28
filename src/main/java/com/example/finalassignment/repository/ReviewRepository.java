// ===================================================================
// 1. 口コミレポジトリ (新規追加)
// ===================================================================
// src/main/java/com/example/finalassignment/repository/ReviewRepository.java

package com.example.finalassignment.repository;

import com.example.finalassignment.dto.AppRatingDto;
import com.example.finalassignment.entity.Review;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 口コミの保存、IDによる検索など、基本的なCRUD操作は
    // JpaRepositoryが提供してくれるので、今は何も書く必要はありません。
    /**
     * 指定されたユーザーIDの口コミを、作成日時の新しい順で全て取得します。
     * @param userId ユーザーID
     * @return 口コミのリスト
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    /**
     * ★★★ 追加: 指定された場所の平均評価と口コミ件数を取得する ★★★
     */
    @Query("SELECT new com.example.finalassignment.dto.AppRatingDto(AVG(r.rating), COUNT(r)) FROM Review r WHERE r.placeId = :placeId")
    AppRatingDto findAppRatingByPlaceId(@Param("placeId") String placeId);

    /**
     * ★★★ 追加: 指定された場所の口コミを新しい順で全て取得する ★★★
     */
    List<Review> findByPlaceIdOrderByCreatedAtDesc(String placeId);
}