package com.aesop.shop.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {

    private Integer rating;
    private String content;
}