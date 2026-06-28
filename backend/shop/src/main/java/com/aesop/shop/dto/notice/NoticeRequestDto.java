package com.aesop.shop.dto.notice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {

    private String title;
    private String content;
    private String author;
}