package com.zerobase.moviereservation.model.dto;

import com.zerobase.moviereservation.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

  private Long id;
  private String username;
  private String movieTitle;
  private String content;
  private Integer rating;

  public static ReviewDto fromEntity(Review review) {
    return ReviewDto.builder()
        .id(review.getId())
        .username(review.getUser().getUsername())
        .movieTitle(review.getReservation().getSchedule().getMovie().getTitle())
        .content(review.getContent())
        .rating(review.getRating())
        .build();
  }
}
