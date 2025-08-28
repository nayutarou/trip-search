package com.example.finalassignment.service;

import com.example.finalassignment.domain.Spot;
import com.example.finalassignment.dto.AppRatingDto;
import com.example.finalassignment.dto.PlaceSearchResultDto;
import com.example.finalassignment.dto.ReviewDisplayDto;
import com.example.finalassignment.entity.Review;
import com.example.finalassignment.entity.User;
import com.example.finalassignment.repository.ReviewRepository;
import com.example.finalassignment.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SpotService {

    // APIキーとエンドポイントURLを一元管理
    @Value("${google.api.key}")
    private String apiKey;
    private static final String PLACES_SEARCH_NEARBY_URL = "https://places.googleapis.com/v1/places:searchNearby";
    private static final String PLACES_GET_DETAILS_BASE_URL = "https://places.googleapis.com/v1/";
    private static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String PLACES_TEXT_SEARCH_URL = "https://places.googleapis.com/v1/places:searchText";
    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate; // ★ JdbcTemplateを追加
    private final ReviewRepository reviewRepository; // ★★★ ReviewRepositoryを注入 ★★★
    private final UserRepository userRepository; // ★★★ UserRepositoryを注入 ★★★

    public SpotService(RestTemplate restTemplate, JdbcTemplate jdbcTemplate, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    /**
     * おすすめの観光地リストを取得します。
     */
    public List<Spot> findRecommendedSpots() {
        // このメソッドは変更なし
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Goog-Api-Key", apiKey);
        headers.add("X-Goog-FieldMask", "places.id,places.displayName,places.formattedAddress,places.rating,places.photos");

        Map<String, Object> body = new HashMap<>();
        body.put("includedTypes", List.of("tourist_attraction"));
        body.put("maxResultCount", 10);
        body.put("languageCode", "ja");
        body.put("locationRestriction", Map.of("circle", Map.of("center", Map.of("latitude", 35.681236, "longitude", 139.767125), "radius", 5000.0)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(PLACES_SEARCH_NEARBY_URL, HttpMethod.POST, entity, String.class);

        // parseSpotsFromResponse()でtry catch分を共通化
        return parseSpotsFromResponse(response.getBody());
    }

    // 口コミ登録機能
    // ★★★ 予測変換用の新しいメソッドを追加 ★★★
    /**
     * キーワードで場所を検索し、予測変換用のDTOリストを返します。
     * @param query 検索キーワード
     * @return PlaceSearchResultDtoのリスト
     */
    public List<PlaceSearchResultDto> searchPlacesForAutocomplete(String query) {
        // 既存の検索メソッドを呼び出す
        List<Spot> spots = searchByKeyword(query);

        // SpotのリストをPlaceSearchResultDtoのリストに変換する
        return spots.stream()
                .map(spot -> new PlaceSearchResultDto(spot.getPlaceId(), spot.getName()))
                .collect(Collectors.toList());
    }

    // ★============ キーワード検索用のメソッドを追加 ============★
    /**
     * 指定されたキーワードで観光地を検索します。
     * @param query 検索キーワード
     * @return 検索結果のSpotリスト
     */
    public List<Spot> searchByKeyword(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Goog-Api-Key", apiKey);
        // 一覧表示で最低限必要な情報を指定
        headers.add("X-Goog-FieldMask", "places.id,places.displayName,places.formattedAddress,places.rating,places.photos");

        Map<String, Object> body = new HashMap<>();
        // ユーザーの入力キーワードに「 観光」という単語を付け加えて、検索の意図を具体的にします。
        body.put("textQuery", query + " 観光");
        body.put("languageCode", "ja");
        body.put("maxResultCount", 10); // 検索結果は10件まで

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Text Search APIを呼び出す
        ResponseEntity<String> response = restTemplate.exchange(PLACES_TEXT_SEARCH_URL, HttpMethod.POST, entity, String.class);

        // JSON解析処理は findRecommendedSpots とほぼ同じなので、共通化するとより良い

        return parseSpotsFromResponse(response.getBody());
    }

    /**
     * APIレスポンスからSpotのリストを生成する共通ヘルパーメソッド
     */
    private List<Spot> parseSpotsFromResponse(String jsonResponse) {
        List<Spot> spots = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode places = root.path("places");

            if (places.isArray()) {
                for (JsonNode place : places) {
                    Spot spot = new Spot();
                    spot.setPlaceId(place.path("id").asText());
                    spot.setName(place.path("displayName").path("text").asText());

                    // 評価と住所のデータをセットする処理を追加します
                    spot.setRating(place.path("rating").asDouble());

                    String originalAddress = place.path("formattedAddress").asText();
                    if (originalAddress.startsWith("日本、")) {
                        spot.setAddress(originalAddress.replaceFirst("日本、", ""));
                    } else {
                        spot.setAddress(originalAddress);
                    }

                    JsonNode photosNode = place.path("photos");
                    if (photosNode.isArray() && photosNode.size() > 0) {
                        String photoName = photosNode.get(0).path("name").asText();
                        String imageUrl = PLACES_GET_DETAILS_BASE_URL + photoName + "/media?maxHeightPx=400&key=" + apiKey;
                        spot.setPhotoUrl(imageUrl);
                    } else {
                        spot.setPhotoUrl("/img/no_image_placeholder.png");
                    }

                    // ★★★ ここからアプリ内評価を取得する処理を追加 ★★★
                    AppRatingDto appRatingDto = reviewRepository.findAppRatingByPlaceId(spot.getPlaceId());

                    if (appRatingDto != null && appRatingDto.getReviewCount() > 0) {
                        // 口コミが存在する場合、評価と件数をセット
                        spot.setAppRating(appRatingDto.getAverageRating());
                        spot.setAppRatingCount(appRatingDto.getReviewCount());
                    } else {
                        // 口コミが存在しない場合、デフォルト値をセット
                        spot.setAppRating(0.0);
                        spot.setAppRatingCount(0);
                    }
                    // ★★★ 追加処理ここまで ★★★

                    spots.add(spot);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spots;
    }

    /**
     * 指定されたplaceIdの観光地の詳細情報を取得します。
     */
    public Spot findDetailsByPlaceId(String placeId) {
        String baseUrl = PLACES_GET_DETAILS_BASE_URL + "places/" + placeId;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("languageCode", "ja");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Goog-Api-Key", apiKey);
        // ★★★ ポイント①: フィールドマスクに "location" を追加 ★★★
        String fieldMask = "id,displayName,formattedAddress,regularOpeningHours,rating,photos,websiteUri,nationalPhoneNumber,editorialSummary,location";
        headers.add("X-Goog-FieldMask", fieldMask);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        String jsonResponse = response.getBody();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode place = objectMapper.readTree(jsonResponse);
            Spot spot = new Spot();

            // 基本情報をセット
            spot.setPlaceId(place.path("id").asText());
            spot.setName(place.path("displayName").path("text").asText());
            spot.setRating(place.path("rating").asDouble());
            spot.setWebsiteUri(place.path("websiteUri").asText());
            spot.setPhoneNumber(place.path("nationalPhoneNumber").asText());

            // 緯度・経度をセット
            JsonNode location = place.path("location");
            spot.setLatitude(location.path("latitude").asDouble());
            spot.setLongitude(location.path("longitude").asDouble());

            // 住所の加工
            String originalAddress = place.path("formattedAddress").asText();
            if (originalAddress.startsWith("日本、")) {
                spot.setAddress(originalAddress.replaceFirst("日本、", ""));
            } else {
                spot.setAddress(originalAddress);
            }

            // 営業時間の加工
            JsonNode openingHoursNode = place.path("regularOpeningHours");
            if (!openingHoursNode.isMissingNode() && openingHoursNode.has("weekdayDescriptions")) {
                JsonNode descriptions = openingHoursNode.path("weekdayDescriptions");
                String hoursText = StreamSupport.stream(descriptions.spliterator(), false)
                        .map(JsonNode::asText)
                        .map(text -> text.replace("曜日", ""))
                        .collect(Collectors.joining("<br>"));
                spot.setOpeningHoursText(hoursText);
            } else {
                spot.setOpeningHoursText("営業時間情報なし");
            }

            // 概要のセット
            JsonNode summaryNode = place.path("editorialSummary");
            if (!summaryNode.isMissingNode()) {
                spot.setDescription(summaryNode.path("text").asText());
            }

            // 写真のセット
            JsonNode photosNode = place.path("photos");
            if (photosNode.isArray() && photosNode.size() > 0) {
                String photoName = photosNode.get(0).path("name").asText();
                String imageUrl = PLACES_GET_DETAILS_BASE_URL + photoName + "/media?maxHeightPx=800&key=" + apiKey;
                spot.setPhotoUrl(imageUrl);
            } else {
                spot.setPhotoUrl("/img/no_image_placeholder.png");
            }

            AppRatingDto appRatingDto = reviewRepository.findAppRatingByPlaceId(spot.getPlaceId());

            if (appRatingDto != null && appRatingDto.getReviewCount() > 0) {
                // 口コミが存在する場合、評価と件数をセット
                spot.setAppRating(appRatingDto.getAverageRating());
                spot.setAppRatingCount(appRatingDto.getReviewCount());
            } else {
                // 口コミが存在しない場合、デフォルト値をセット
                spot.setAppRating(0.0);
                spot.setAppRatingCount(0);
            }

            // ★★★ ここから口コミリストを取得する処理を追加 ★★★
            List<Review> reviews = reviewRepository.findByPlaceIdOrderByCreatedAtDesc(spot.getPlaceId());

            if (reviews.isEmpty()) {
                spot.setReviews(Collections.emptyList());
            } else {
                List<Long> userIds = reviews.stream()
                        .map(Review::getUserId)
                        .distinct()
                        .collect(Collectors.toList());

                // ★★★ ここでRepositoryのメソッドを呼び出す ★★★
                Map<Long, User> userMap = userRepository.findByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getUserId, user -> user));

                List<ReviewDisplayDto> reviewDtos = reviews.stream()
                        .map(review -> {
                            User defaultUser = new User("退会したユーザー");
                            String userEmail = userMap.getOrDefault(review.getUserId(), defaultUser).getEmail();
                            return new ReviewDisplayDto(review, userEmail);
                        })
                        .collect(Collectors.toList());

                spot.setReviews(reviewDtos);
            }

            // ★★★ ポイント②: ここからアクセス時間生成の連携処理 ★★★
            System.out.println("--- アクセス時間生成処理 開始 ---");
            System.out.println("目的地の座標: " + spot.getLatitude() + ", " + spot.getLongitude());

            // 1. 最寄り駅を検索
            Map<String, String> nearestStation = findNearestStation(spot.getLatitude(), spot.getLongitude());

            if (nearestStation != null) {
                System.out.println("最寄り駅を発見: " + nearestStation.get("name") + " (ID: " + nearestStation.get("placeId") + ")");

                // 2. 目的地IDから "places/" を削除
                String destinationId = spot.getPlaceId().replace("places/", "");

                // 3. 最寄り駅から目的地までの「車」での所要時間を計算
                String travelTime = getTravelTime(nearestStation.get("placeId"), destinationId, "driving");

                if (travelTime != null) {
                    System.out.println("所要時間を取得: " + travelTime);
                    // 4. アクセス情報テキストを生成してセット
                    String accessText = nearestStation.get("name") + "から車で約" + travelTime;
                    spot.setAccessInfo(accessText);
                    System.out.println("最終的なアクセス情報: " + accessText);
                } else {
                    System.out.println("【エラー】所要時間の取得に失敗しました。");
                }
            } else {
                System.out.println("【エラー】最寄り駅が見つかりませんでした。");
            }

            // アクセス情報が見つからなかった場合のデフォルト値
            if (spot.getAccessInfo() == null) {
                spot.setAccessInfo("アクセス情報なし");
            }

            System.out.println("--- アクセス時間生成処理 終了 ---");

            return spot;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    口コミ登録の機能
    /**
     * 指定された場所がDBに存在することを保証します。
     * 存在しない場合のみ、APIから最小限の情報を取得してDBに保存します。
     * @param placeId 場所のID
     */
    @Transactional
    public void ensurePlaceExists(String placeId) {
        String checkSql = "SELECT COUNT(*) FROM places WHERE place_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, placeId);

        // DBに存在しない場合のみ、APIから取得して保存する
        if (count == 0) {
            System.out.println("Place not in DB, fetching and saving minimal details for: " + placeId);
            fetchAndSavePlaceDetails(placeId);
        } else {
            System.out.println("Place already exists in DB: " + placeId);
        }
    }

    /**
     * Google APIから場所の詳細を取得し、DBに保存するヘルパーメソッド
     * (ensurePlaceExistsからのみ使用)
     */
    private void fetchAndSavePlaceDetails(String placeId) {
        String baseUrl = "https://places.googleapis.com/v1/places/" + placeId;
        String fieldMask = "id,displayName,formattedAddress,location,websiteUri,nationalPhoneNumber,rating,userRatingCount,priceLevel";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("languageCode", "ja")
                .queryParam("fields", fieldMask);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Goog-Api-Key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
            JsonNode place = new ObjectMapper().readTree(response.getBody());

            String insertSql = "INSERT INTO places (place_id, name, address, latitude, longitude, website_url, phone_number, rating_google, user_ratings_total, price_level) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(insertSql,
                    place.path("id").asText(),
                    place.path("displayName").path("text").asText(""),
                    place.path("formattedAddress").asText(""),
                    place.path("location").path("latitude").asDouble(0.0),
                    place.path("location").path("longitude").asDouble(0.0),
                    place.path("websiteUri").asText(null),
                    place.path("nationalPhoneNumber").asText(null),
                    place.path("rating").isMissingNode() ? null : BigDecimal.valueOf(place.path("rating").asDouble()),
                    place.path("userRatingCount").asInt(0),
                    place.path("priceLevel").asText(null)
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch and save place details from Google API.", e);
        }
    }


    /**
     * 指定された座標から最も近い駅を検索します。
     */
    private Map<String, String> findNearestStation(double lat, double lng) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Goog-Api-Key", apiKey);
        headers.add("X-Goog-FieldMask", "places.id,places.displayName");

        Map<String, Object> body = new HashMap<>();
        body.put("includedTypes", List.of("train_station", "subway_station"));
        body.put("maxResultCount", 1);
        body.put("rankPreference", "DISTANCE"); // 距離順で検索
        body.put("languageCode", "ja");

        // 正しい階層構造で中心座標と検索半径を指定します
        Map<String, Object> center = new HashMap<>();
        center.put("latitude", lat);
        center.put("longitude", lng);

        Map<String, Object> circle = new HashMap<>();
        circle.put("center", center);
        circle.put("radius", 5000.0); // 半径5km以内で検索

        // locationRestrictionの中にcircleを入れるのが正しい形式です
        body.put("locationRestriction", Map.of("circle", circle));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(PLACES_SEARCH_NEARBY_URL, HttpMethod.POST, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode places = root.path("places");

            if (places.isArray() && places.size() > 0) {
                JsonNode station = places.get(0);
                Map<String, String> stationInfo = new HashMap<>();
                stationInfo.put("name", station.path("displayName").path("text").asText());
                stationInfo.put("placeId", station.path("id").asText().replace("places/", ""));
                return stationInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 出発地から目的地までの所要時間を取得します。
     */
    private String getTravelTime(String originPlaceId, String destinationPlaceId, String mode) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DIRECTIONS_API_URL)
                    .queryParam("origin", "place_id:" + originPlaceId)
                    .queryParam("destination", "place_id:" + destinationPlaceId)
                    .queryParam("language", "ja")
                    .queryParam("mode", mode)
                    .queryParam("key", apiKey);

            String jsonResponse = restTemplate.getForObject(builder.toUriString(), String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode routes = root.path("routes");

            if (routes.isArray() && routes.size() > 0) {
                return routes.get(0).path("legs").get(0).path("duration").path("text").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
