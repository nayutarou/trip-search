package com.example.finalassignment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // ★★★ JPAのために引数なしコンストラクタを追加 ★★★
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * ★★★ SpotServiceで使うためのコンストラクタを追加 ★★★
     * これで new User("退会したユーザー") が呼び出せるようになります。
     * @param email セットしたいメールアドレス
     */
    public User(String email) {
        this.email = email;
    }
}