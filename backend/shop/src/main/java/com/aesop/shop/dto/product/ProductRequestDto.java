package com.aesop.shop.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {

    private Long categoryId;
    private String name;
    private String description;
    private String volume;
    private String skinType;
    private String ingredients;
    private Integer price;
    private Integer stock;
    private Boolean isBestseller;
}