package com.aesop.shop.controller;

import com.aesop.shop.dto.member.MemberRequestDto;
import com.aesop.shop.dto.member.MemberResponseDto;
import com.aesop.shop.entity.Member;
import com.aesop.shop.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @PostMapping({"/signup", "/api/members/signup"})
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

    @PostMapping("/api/members/login")
    public ResponseEntity<MemberResponseDto> login(
            @RequestBody MemberRequestDto dto,
            HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context);

        Member member = memberService.findByEmail(dto.getEmail());
        return ResponseEntity.ok(new MemberResponseDto(member));
    }

    @PostMapping("/api/members/logout")
    public ResponseEntity<Void> apiLogout(HttpServletRequest request) {
        request.getSession(false).invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/mypage", "/api/members/me"})
    public ResponseEntity<MemberResponseDto> getMypage(
            @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(new MemberResponseDto(member));
    }

    @PutMapping({"/mypage", "/api/members/me"})
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

    @PutMapping("/mypage/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        memberService.changePassword(member.getId(), currentPassword, newPassword);
        return ResponseEntity.ok("Password changed");
    }

    @DeleteMapping("/mypage")
    public ResponseEntity<String> deleteMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String password) {
        Member member = memberService.findByEmail(userDetails.getUsername());
        memberService.deleteMember(member.getId(), password);
        return ResponseEntity.ok("Member deleted");
    }
}
