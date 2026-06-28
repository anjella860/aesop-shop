package com.aesop.shop.dto.member;

import com.aesop.shop.entity.Member;
import com.aesop.shop.entity.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private LocalDateTime createdAt;

    // Entity → DTO 변환
    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.role = member.getRole();
        this.createdAt = member.getCreatedAt();
    }
}