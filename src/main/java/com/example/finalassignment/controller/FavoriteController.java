package com.example.finalassignment.controller;

import com.example.finalassignment.dto.FavoriteSpotDto;
import com.example.finalassignment.service.FavoriteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * お気に入り一覧ページに関するリクエストを処理するコントローラー。
 */
@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String showFavoriteListPage(Model model) {
        Long currentUserId = 1L; // 仮のユーザーID
        // Serviceを呼び出して、お気に入り場所の詳細情報リストを取得
        List<FavoriteSpotDto> favoriteSpots = favoriteService.findFavoriteDetailsByUserId(currentUserId);

        model.addAttribute("favoriteSpots", favoriteSpots);
        return "/favorite/favorite"; // templates/favorite/favorite.html を呼び出す
    }
}
