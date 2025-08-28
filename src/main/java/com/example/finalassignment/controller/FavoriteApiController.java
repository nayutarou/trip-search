package com.example.finalassignment.controller;

import com.example.finalassignment.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * お気に入り機能に関する非同期リクエスト(API)を処理するコントローラー。
 * JavaScriptからのリクエストを受け取り、JSONやステータスコードを返します。
 */
@RestController
@RequestMapping("/api/favorites") // JavaScriptからのリクエスト先URL
public class FavoriteApiController {

    private final FavoriteService favoriteService;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteApiController.class);

    public FavoriteApiController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * お気に入りに追加するAPI
     * @param placeId パスから受け取る場所ID
     * @return 成功(200 OK) or 失敗
     */
    @PostMapping("/{placeId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String placeId) {
        try {
            // ★★★ ログイン機能が実装されるまでは、仮のユーザーID(1L)を使用 ★★★
            Long currentUserId = 1L;
            logger.info("API経由でお気に入り登録リクエストを受信. userId: {}, placeId: {}", currentUserId, placeId);
            favoriteService.addFavorite(placeId, currentUserId);
            return ResponseEntity.ok().build(); // 成功したことを示す 200 OK を返す
        } catch (Exception e) {
            logger.error("お気に入り登録APIでエラーが発生しました. placeId: {}", placeId, e);
            return ResponseEntity.badRequest().build(); // 何らかのエラーを示す 400 Bad Request を返す
        }
    }

    /**
     * お気に入りから削除するAPI
     * @param placeId パスから受け取る場所ID
     * @return 成功(200 OK) or 失敗
     */
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String placeId) {
        try {
            // ★★★ ログイン機能が実装されるまでは、仮のユーザーID(1L)を使用 ★★★
            Long currentUserId = 1L;
            logger.info("API経由でお気に入り解除リクエストを受信. userId: {}, placeId: {}", currentUserId, placeId);
            favoriteService.removeFavorite(placeId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("お気に入り解除APIでエラーが発生しました. placeId: {}", placeId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /*
     * 補足：
     * Spring Securityでログイン機能を実装した後は、
     * 各メソッドの引数に @AuthenticationPrincipal UserDetails userDetails を追加し、
     * ログインユーザーの情報を取得するのが一般的です。
     */
}
