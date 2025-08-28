package com.example.finalassignment.controller;

import com.example.finalassignment.domain.Spot;
import com.example.finalassignment.service.FavoriteService;
import com.example.finalassignment.service.SpotService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 観光地に関するリクエストを処理するコントローラー
 */
@Controller
@RequestMapping({"/spots", "/", "/spots/"})
public class TouristSpotController {

    private final SpotService spotService;
    private final FavoriteService favoriteService; // ★ FavoriteServiceを注入

    public TouristSpotController(SpotService spotService, FavoriteService favoriteService) {
        this.spotService = spotService;
        this.favoriteService = favoriteService;
    }

    /**
     * 観光地検索画面を表示します。
     *
     * @return 検索画面のテンプレート名
     */
    // ★============ 検索とおすすめ表示を兼ねるメソッドに修正 ============★

    /**
     * 観光地の一覧画面を表示します。
     * リクエストパラメータに'query'があればキーワード検索を、なければおすすめを表示します。
     *
     * @param query 検索キーワード (任意)
     * @param model ビューにデータを渡すためのモデル
     * @return 一覧表示画面のテンプレート名
     */
    @GetMapping
    public String showListPage(@RequestParam(name = "query", required = false) String query, Model model) {

        List<Spot> spots;

        // queryパラメータの有無で処理を分岐
        if (query != null && !query.isEmpty()) {
            // ① キーワード検索の場合
            spots = spotService.searchByKeyword(query);
            model.addAttribute("searchQuery", query); // 検索キーワードをHTMLに渡す
        } else {
            // ② おすすめ表示の場合 (初回アクセス時)
            spots = spotService.findRecommendedSpots();
        }

        model.addAttribute("spots", spots);
        return "/spot/search"; // 表示するHTMLは同じ
    }
// ★============ 詳細ページ用のメソッドをここに追加 ============★

    /**
     * 指定されたplaceIdの観光地の詳細画面を表示します。
     *
     * @param placeId URLから受け取る場所のID
     * @param model   ビューにデータを渡すためのモデル
     * @return 詳細画面のテンプレート名
     */
    @GetMapping("/{placeId}")
    public String showDetailPage(@PathVariable String placeId, Model model) {
        Spot spot = spotService.findDetailsByPlaceId(placeId);
        if (spot == null) {
            return "redirect:/spots";
        }

        // ★ FavoriteServiceのメソッドを呼び出すように修正
        Long currentUserId = 1L;
        boolean isFavorite = favoriteService.isFavorite(spot.getPlaceId(), currentUserId);
        model.addAttribute("isFavorite", isFavorite);

        model.addAttribute("spot", spot);
        return "/spot/detail";
    }

    @PostMapping("/{placeId}/favorite")
    public String addFavorite(@PathVariable String placeId) {
        Long currentUserId = 1L;
        // ★ FavoriteServiceのメソッドを呼び出すように修正
        favoriteService.addFavorite(placeId, currentUserId);
        return "redirect:/spots/" + placeId;
    }

    @PostMapping("/{placeId}/unfavorite")
    public String removeFavorite(@PathVariable String placeId) {
        Long currentUserId = 1L;
        // ★ FavoriteServiceのメソッドを呼び出すように修正
        favoriteService.removeFavorite(placeId, currentUserId);
        return "redirect:/spots/" + placeId;
    }

}

