// ===================================================================
// 3. 口コミコントローラー (更新)
// ===================================================================
// src/main/java/com/example/finalassignment/controller/ReviewController.java

package com.example.finalassignment.controller;

import com.example.finalassignment.domain.Spot;
import com.example.finalassignment.dto.MyReviewDto;
import com.example.finalassignment.dto.ReviewForm;
import com.example.finalassignment.entity.Review;
import com.example.finalassignment.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    // SpotServiceはフォーム表示時に場所名を取得するために必要なので残します
    private final com.example.finalassignment.service.SpotService spotService;

    public ReviewController(ReviewService reviewService, com.example.finalassignment.service.SpotService spotService) {
        this.reviewService = reviewService;
        this.spotService = spotService;
    }

    /**
     * 口コミ投稿フォーム画面を表示します。
     * ★★★ このメソッドはGETリクエスト用なので変更なし ★★★
     */
    @GetMapping("/new")
    public String showReviewForm(Model model) {
        // フォームを初期化
        model.addAttribute("reviewForm", new ReviewForm());
        return "review/form";
    }

    /**
     * 口コミの投稿を処理します。
     * @param reviewForm フォームから送信されたデータ (@ModelAttributeで受け取る)
     * @return 投稿完了後は、ホームページなどにリダイレクト
     */
    @PostMapping
    public String createReview(@ModelAttribute ReviewForm reviewForm) {
        // 仮のユーザーIDを使用
        Long currentUserId = 1L;
        reviewService.createReview(reviewForm, currentUserId);

        // 投稿後は、ホームページなど適切なページに戻る
        return "redirect:/spots";
    }

    /**
     * 自分の口コミ一覧ページを表示します。
     */
    @GetMapping("/my-list")
    public String showMyReviewList(Model model) {
        // 仮のユーザーIDを使用
        Long currentUserId = 1L;
        List<MyReviewDto> myReviews = reviewService.findMyReviews(currentUserId);

        model.addAttribute("myReviews", myReviews);
        return "review/list"; // templates/review/list.html を表示
    }

    /**
     * ★★★ 編集フォームを表示するメソッドを追加 ★★★
     */
    @GetMapping("/{reviewId}/edit")
    public String showEditForm(@PathVariable Long reviewId, Model model) {
        Review review = reviewService.findReviewById(reviewId);
        Spot spot = spotService.findDetailsByPlaceId(review.getPlaceId());

        // EntityからForm DTOへの変換
        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setReviewId(review.getReviewId());
        reviewForm.setPlaceId(review.getPlaceId());
        reviewForm.setRating(review.getRating());
        reviewForm.setComment(review.getComment());
        reviewForm.setVisitedAt(review.getVisitedAt());
        reviewForm.setExistingImagePath(review.getImagePath());

        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("placeName", spot.getName()); // 場所名を表示するために追加
        model.addAttribute("isEditMode", true); // 編集モードであることをビューに伝える
        return "review/form"; // 新規登録と同じフォームを再利用
    }

    /**
     * ★★★ 更新処理を行うメソッドを追加 ★★★
     */
    @PostMapping("/update")
    public String updateReview(@ModelAttribute ReviewForm reviewForm) {
        reviewService.updateReview(reviewForm);
        return "redirect:/reviews/my-list";
    }

    /**
     * ★★★ 削除処理を行うメソッドを追加 ★★★
     */
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return "redirect:/reviews/my-list";
    }
}
