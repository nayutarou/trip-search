package com.example.finalassignment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "favoritePlaces")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_place_id")
    private Long favoritePlaceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "place_id", nullable = false)
    private String placeId;

    // ★★★ 修正点1: insertable=false を削除 ★★★
    // これにより、Java側でセットした日時をINSERT文に含めるようになります。
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Favorite(Long userId, String placeId) {
        this.userId = userId;
        this.placeId = placeId;
    }

    // ★★★ 修正点2: @PrePersistメソッドの追加 ★★★
    // このエンティティがデータベースに保存される直前に、このメソッドが自動で実行されます。
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
