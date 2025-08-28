// src/main/java/com/example/finalassignment/controller/PlaceApiController.java

package com.example.finalassignment.controller;

import com.example.finalassignment.dto.PlaceSearchResultDto;
import com.example.finalassignment.service.SpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 場所に関する非同期リクエスト(API)を処理するコントローラー
 */
@RestController
@RequestMapping("/api/places")
public class PlaceApiController {

    private final SpotService spotService;

    public PlaceApiController(SpotService spotService) {
        this.spotService = spotService;
    }

    /**
     * 指定されたキーワードで場所を検索し、予測変換用のリストを返します。
     * @param query 検索キーワード
     * @return 場所のIDと名前のリスト (JSON形式)
     */
    @GetMapping("/search")
    public ResponseEntity<List<PlaceSearchResultDto>> searchPlaces(@RequestParam(name = "query", required = false) String query) {
        // クエリが空、またはnullの場合は空のリストを返す
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of()); // 空のリスト
        }

        List<PlaceSearchResultDto> results = spotService.searchPlacesForAutocomplete(query);
        return ResponseEntity.ok(results);
    }
}