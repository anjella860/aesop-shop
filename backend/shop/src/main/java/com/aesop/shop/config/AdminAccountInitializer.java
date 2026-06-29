package com.aesop.shop.config;

import com.aesop.shop.entity.Member;
import com.aesop.shop.entity.Role;
import com.aesop.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "1234";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Member admin = memberRepository.findByEmail(ADMIN_EMAIL)
                .orElseGet(() -> Member.builder()
                        .email(ADMIN_EMAIL)
                        .name("관리자")
                        .phone("010-0000-0000")
                        .address("테스트")
                        .build());

        admin.setName("관리자");
        admin.setPhone("010-0000-0000");
        admin.setAddress("테스트");
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        memberRepository.save(admin);
    }
}
