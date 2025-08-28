package com.example.finalassignment.domain;

import com.example.finalassignment.entity.Review;
import com.example.finalassignment.dto.ReviewDisplayDto; // ★★★ 型を変更 ★★★
import java.util.List;

public class Spot {
    // ここにname, address, ratingなどのフィールドを追加していく
    private String name;        // 場所の名前
    private double rating;      // 評価
    private String address;     // 住所
    private String placeId;     // Google Place ID (詳細表示で使います)
    private String photoUrl;    // 写真のURL
    private String openingHoursText; // ★ 営業時間を表す文字列を追加
    private String phoneNumber;// 電話番号
    private String websiteUri;// 公式サイトURL
    private String description; // ★ 概要を格納するフィールドを追加
    private double latitude;
    private double longitude;
    private String accessInfo; // ★ アクセス情報を格納するフィールドを追加
    // ★★★ 追加: アプリ内評価用のフィールド ★★★
    private double appRating;
    private long appRatingCount;
    // ★★★ ReviewのリストからReviewDisplayDtoのリストに変更 ★★★
    private List<ReviewDisplayDto> reviews;


    // --- 以下、ゲッターとセッター ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    // ★ Getter and Setter for the new field
    public String getOpeningHoursText() {
        return openingHoursText;
    }

    public void setOpeningHoursText(String openingHoursText) {
        this.openingHoursText = openingHoursText;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAccessInfo() {
        return accessInfo;
    }

    public void setAccessInfo(String accessInfo) {
        this.accessInfo = accessInfo;
    }

    public double getAppRating() {
        return appRating;
    }

    public void setAppRating(double appRating) {
        this.appRating = appRating;
    }

    public long getAppRatingCount() {
        return appRatingCount;
    }

    public void setAppRatingCount(long appRatingCount) {
        this.appRatingCount = appRatingCount;
    }

    public List<ReviewDisplayDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDisplayDto> reviews) {
        this.reviews = reviews;
    }
}

