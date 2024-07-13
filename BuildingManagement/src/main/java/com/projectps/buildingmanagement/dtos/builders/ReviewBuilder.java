package com.projectps.buildingmanagement.dtos.builders;

import com.projectps.buildingmanagement.dtos.ReviewDTO;
import com.projectps.buildingmanagement.entities.Review;
import com.projectps.buildingmanagement.entities.User;

public class ReviewBuilder {

    public static ReviewDTO toReviewDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .message(review.getMessage())
                .date(review.getDate())
                .user_id(review.getUser().getId())
                .build();
    }

    public static Review toEntity(ReviewDTO reviewDTO) {
        return Review.builder()
                .id(reviewDTO.getId())
                .message(reviewDTO.getMessage())
                .date(reviewDTO.getDate())
                .user(User.builder()
                        .id(reviewDTO.getUser_id())
                        .build())
                .build();
    }
}
