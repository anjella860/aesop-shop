package com.aesop.shop.repository;

import com.aesop.shop.entity.Member;
import com.aesop.shop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //회원가입(save)
    //회원목록(findAll)
    //특정회원정보조회(findById)
    //회원 정보수정(findById, Save)
    //회원탈퇴(deleteById)

    //이메일 회원 정보 조회
    Optional<Member> findByEmail(String email);

    //이메일 중복 확인
    boolean existsByEmail(String email);

    //권한별 조회
    List<Member> findByRole(Role role);
    //ROLE_ADMIN, ROLE_MANAGER, ROUE_USER222


    // 이메일 키워드  검색 (findByEmailContaining)
    List<Member> findByEmailContaining(String email);

    //  주소 키워드 검색 (findByAddressContaining)
    List<Member> findByAddressContaining(String address);
}

