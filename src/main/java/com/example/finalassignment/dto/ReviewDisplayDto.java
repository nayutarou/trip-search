package com.example.finalassignment.dto;

import com.example.finalassignment.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 口コミとその投稿者名を一緒に格納するためのDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDisplayDto {
    private Review review;
    private String email;
}