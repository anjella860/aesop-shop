package com.aesop.shop.controller;

import com.aesop.shop.dto.member.MemberRequestDto;
import com.aesop.shop.dto.member.MemberResponseDto;
import com.aesop.shop.entity.Member;
import com.aesop.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto dto) {
        Member member = Member.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
        Member saved = memberService.signup(member);
        return ResponseEntity.ok(new MemberResponseDto(saved));
    }

    // 내 정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<MemberResponseDto> getMypage(
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(new MemberResponseDto(member));
    }

    // 회원 정보 수정
    @PutMapping("/mypage")
    public ResponseEntity<MemberResponseDto> updateMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MemberRequestDto dto) {
        Member loginMember = memberService.findByEmail(userDetails.getUsername());
        Member updateMember = Member.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
        Member updated = memberService.updateMember(loginMember.getId(), updateMember);
        return ResponseEntity.ok(new MemberResponseDto(updated));
    }

    // 비밀번호 변경
    @PutMapping("/mypage/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        memberService.changePassword(member.getId(), currentPassword, newPassword);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }

    // 회원 탈퇴
    @DeleteMapping("/mypage")
    public ResponseEntity<String> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String password) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        memberService.deleteMember(member.getId(), password);
        return ResponseEntity.ok("탈퇴 완료");
    }
}