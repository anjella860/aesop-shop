package com.aesop.shop.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequestDto {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
}