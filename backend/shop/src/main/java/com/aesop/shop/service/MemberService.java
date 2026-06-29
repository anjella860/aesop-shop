package com.aesop.shop.service;

import com.aesop.shop.entity.Member;
import com.aesop.shop.entity.Role;
import com.aesop.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public Member signup(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole(Role.USER);
        return memberRepository.save(member);
    }

    // 이메일로 회원 조회 (Spring Security용)
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }

    // 회원 정보 수정
    public Member updateMember(Long id, Member updateMember) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        member.setName(updateMember.getName());
        member.setPhone(updateMember.getPhone());
        member.setAddress(updateMember.getAddress());
        return memberRepository.save(member);
    }

    // 비밀번호 변경
    public void changePassword(Long id, String currentPassword, String newPassword) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    // 회원 탈퇴
    public void deleteMember(Long id, String password) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        memberRepository.deleteById(id);
    }

    // 전체 회원 목록 (관리자)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 권한별 회원 조회 (관리자)
    public List<Member> findByRole(Role role) {
        return memberRepository.findByRole(role);
    }

    // 회원 등급 변경 (관리자)
    public void changeRole(Long id, Role role) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        member.setRole(role);
        memberRepository.save(member);
    }

    // 회원 강퇴 (관리자)
    public void deleteMemberByAdmin(Long id) {
        memberRepository.deleteById(id);
    }

    // 이메일 키워드 검색 (관리자)
    public List<Member> findByEmailContaining(String email) {
        return memberRepository.findByEmailContaining(email);
    }
}