// ===================================================================
// 2. 口コミサービス (更新)
// ===================================================================
// src/main/java/com/example/finalassignment/service/ReviewService.java

package com.example.finalassignment.service;

import com.example.finalassignment.domain.Spot;
import com.example.finalassignment.dto.MyReviewDto;
import com.example.finalassignment.dto.ReviewForm;
import com.example.finalassignment.entity.Review;
import com.example.finalassignment.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService; // ★★★ 画像保存サービスを注入 ★★★
    private final SpotService spotService; // ★★★ SpotServiceを注入 ★★★

    public ReviewService(ReviewRepository reviewRepository, FileStorageService fileStorageService, SpotService spotService) {
        this.reviewRepository = reviewRepository;
        this.fileStorageService = fileStorageService;
        this.spotService = spotService;
    }

    /**
     * フォームから受け取った情報をもとに、口コミを保存します。
     * @param reviewForm フォームの入力データ
     * @param userId 投稿したユーザーのID
     */
    @Transactional
    public void createReview(ReviewForm reviewForm, Long userId) {
        // ★★★ ここに処理を追加 ★★★
        // 口コミを登録する前に、対象の場所がplacesテーブルに存在することを保証する。
        // findDetailsByPlaceIdは、DBになければGoogle APIから取得して保存するロジックを持つ。
        spotService.ensurePlaceExists(reviewForm.getPlaceId());

        Review review = new Review();
        review.setUserId(userId);
        review.setPlaceId(reviewForm.getPlaceId());
        review.setRating(reviewForm.getRating());
        review.setComment(reviewForm.getComment());
        review.setVisitedAt(reviewForm.getVisitedAt());

        // ★★★ 画像ファイルの処理を追加 ★★★
        MultipartFile imageFile = reviewForm.getImageFile();
        // 画像ファイルが添付されていて、空でない場合のみ保存処理を行う
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileStorageService.storeFile(imageFile);
            review.setImagePath(imagePath); // 保存した画像のパスをエンティティにセット
        }

        reviewRepository.save(review);
    }

    /**
     * 指定されたユーザーの口コミ一覧を取得します。
     * @param userId ユーザーID
     * @return 表示用の口コミ情報リスト
     */
    @Transactional(readOnly = true)
    public List<MyReviewDto> findMyReviews(Long userId) {
        // 1. DBからユーザーの口コミを全て取得
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 2. 各口コミに対応する場所の詳細情報を取得し、DTOに詰める
        return reviews.stream()
                .map(review -> {
                    // 3. 場所の詳細情報を取得
                    Spot spot = spotService.findDetailsByPlaceId(review.getPlaceId());
                    // 4. ReviewとSpotをDTOにまとめて返す
                    return new MyReviewDto(review, spot);
                })
                .collect(Collectors.toList());
    }

    /**
     * ★★★ IDで口コミを検索するメソッドを追加 ★★★
     */
    @Transactional(readOnly = true)
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));
    }

    /**
     * ★★★ 口コミを更新するメソッドを追加 ★★★
     */
    @Transactional
    public void updateReview(ReviewForm reviewForm) {
        Review review = findReviewById(reviewForm.getReviewId());

        // フォームの内容でエンティティを更新
        review.setRating(reviewForm.getRating());
        review.setComment(reviewForm.getComment());
        review.setVisitedAt(reviewForm.getVisitedAt());

        // 新しい画像ファイルがアップロードされたかチェック
        MultipartFile imageFile = reviewForm.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            // 既存の画像を削除
            fileStorageService.deleteFile(review.getImagePath());
            // 新しい画像を保存
            String newImagePath = fileStorageService.storeFile(imageFile);
            review.setImagePath(newImagePath);
        }

        reviewRepository.save(review);
    }

    /**
     * ★★★ 口コミを削除するメソッドを追加 ★★★
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = findReviewById(reviewId);

        // 関連する画像を削除
        fileStorageService.deleteFile(review.getImagePath());

        // 口コミをDBから削除
        reviewRepository.delete(review);
    }
}